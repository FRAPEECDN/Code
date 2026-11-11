package com.fp.boot_demo_web.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fp.boot_demo_web.exceptions.EntityNotFoundException;
import com.fp.boot_demo_web.model.dto.CourseDto;
import com.fp.boot_demo_web.model.dto.DepartmentDto;
import com.fp.boot_demo_web.model.dto.StudentDto;
import com.fp.boot_demo_web.model.enums.CourseLevel;
import com.fp.boot_demo_web.model.enums.DepartmentType;
import com.fp.boot_demo_web.repository.CourseRepository;
import com.fp.boot_demo_web.repository.DepartmentRepository;
import com.fp.boot_demo_web.repository.StudentRepository;

/** H2-backed service tests for department and course lifecycle operations. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ DepartmentService.class, StudentService.class })
@ActiveProfiles("test")
class TestDepartmentService {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void managesDepartmentAndCourseCrudAndProtectsRegisteredCourses() {
        DepartmentDto firstDepartment = departmentService.createOne(
                new DepartmentDto(null, "First Department", DepartmentType.SCIENCE, Set.of()));
        DepartmentDto secondDepartment = departmentService.createOne(
                new DepartmentDto(null, "Second Department", DepartmentType.ARTS, Set.of()));
        assertThat(departmentService.getAll()).extracting(DepartmentDto::id)
                .contains(firstDepartment.id(), secondDepartment.id());

        CourseDto createdCourse = departmentService.createCourse(courseInput(
                "DEP-101", "Department Foundations", firstDepartment.id(), Set.of()));
        assertThat(departmentService.getCourses(firstDepartment.id())).containsExactly(createdCourse);
        assertThat(departmentService.getCourse(firstDepartment.id(), createdCourse.id())).isEqualTo(createdCourse);

        CourseDto movedCourse = departmentService.updateCourse(createdCourse.id(),
                new CourseDto(createdCourse.id(), "DEP-201", "Updated Foundations", 225.0, false,
                        CourseLevel.INTERMEDIATE, secondDepartment.id(), Set.of()));
        assertThat(movedCourse.code()).isEqualTo("DEP-201");
        assertThat(movedCourse.departmentId()).isEqualTo(secondDepartment.id());
        assertThat(departmentService.getCourses(firstDepartment.id())).isEmpty();
        assertThat(departmentService.getCourses(secondDepartment.id())).containsExactly(movedCourse);

        DepartmentDto updatedDepartment = departmentService.updateOne(secondDepartment.id(),
                new DepartmentDto(secondDepartment.id(), "Updated Department", DepartmentType.BUSINESS,
                        Set.of(movedCourse.id())));
        assertThat(updatedDepartment.name()).isEqualTo("Updated Department");
        assertThat(updatedDepartment.courseIds()).containsExactly(movedCourse.id());
        assertThatThrownBy(() -> departmentService.updateOne(secondDepartment.id(),
                new DepartmentDto(secondDepartment.id(), "Invalid Course List", DepartmentType.BUSINESS, Set.of())))
                .isInstanceOf(IllegalArgumentException.class);

        StudentDto student = studentService.createOne(
                new StudentDto(null, "Registered Student", 1, true, null, Set.of()));
        studentService.registerForCourse(student.id(), movedCourse.id());

        assertThatThrownBy(() -> departmentService.deleteCourse(movedCourse.id()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> departmentService.deleteOne(secondDepartment.id()))
                .isInstanceOf(IllegalStateException.class);

        studentService.deregisterFromCourse(student.id(), movedCourse.id());
        departmentService.deleteCourse(movedCourse.id());
        departmentService.deleteOne(secondDepartment.id());
        departmentService.deleteOne(firstDepartment.id());

        assertThat(courseRepository.findById(movedCourse.id())).isEmpty();
        assertThat(departmentRepository.findById(firstDepartment.id())).isEmpty();
        assertThat(departmentRepository.findById(secondDepartment.id())).isEmpty();
        assertThat(studentRepository.findById(student.id())).isPresent();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void rollsBackInvalidCourseUpdate() {
        DepartmentDto department = departmentService.createOne(
                new DepartmentDto(null, "Rollback Department", DepartmentType.SCIENCE, Set.of()));
        CourseDto course = departmentService.createCourse(courseInput(
                "ROLLBACK-101", "Original Course", department.id(), Set.of()));

        assertThatThrownBy(() -> departmentService.updateCourse(course.id(),
                new CourseDto(course.id(), "ROLLBACK-101", "x".repeat(101), 100.0, true,
                        CourseLevel.INTRODUCTORY, department.id(), Set.of())))
                .isInstanceOf(TransactionSystemException.class)
                .hasRootCauseInstanceOf(ConstraintViolationException.class);

        CourseDto stored = departmentService.getCourse(department.id(), course.id());
        assertThat(stored.title()).isEqualTo("Original Course");
        assertThat(stored.tuition()).isEqualTo(100.0);
    }

    @Test
    void rejectsCoursesOutsideRequestedDepartment() {
        DepartmentDto department = departmentService.createOne(
                new DepartmentDto(null, "Lookup Department", DepartmentType.SCIENCE, Set.of()));
        DepartmentDto otherDepartment = departmentService.createOne(
                new DepartmentDto(null, "Other Department", DepartmentType.ARTS, Set.of()));
        CourseDto course = departmentService.createCourse(courseInput(
                "LOOKUP-101", "Lookup Course", department.id(), Set.of()));

        assertThatThrownBy(() -> departmentService.getCourse(otherDepartment.id(), course.id()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private static CourseDto courseInput(String code, String title, Integer departmentId, Set<Integer> studentIds) {
        return new CourseDto(null, code, title, 100.0, true, CourseLevel.INTRODUCTORY, departmentId, studentIds);
    }
}
