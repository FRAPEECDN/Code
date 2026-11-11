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
import com.fp.boot_demo_web.model.dto.StudentDto;
import com.fp.boot_demo_web.model.dto.StudentProfileDto;
import com.fp.boot_demo_web.model.entities.CourseEntity;
import com.fp.boot_demo_web.model.entities.DepartmentEntity;
import com.fp.boot_demo_web.model.entities.StudentEntity;
import com.fp.boot_demo_web.model.enums.CourseLevel;
import com.fp.boot_demo_web.model.enums.DepartmentType;
import com.fp.boot_demo_web.repository.CourseRepository;
import com.fp.boot_demo_web.repository.DepartmentRepository;
import com.fp.boot_demo_web.repository.StudentProfileRepository;
import com.fp.boot_demo_web.repository.StudentRepository;

/** H2-backed service tests for student CRUD, registrations, and profiles. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StudentService.class)
@ActiveProfiles("test")
class TestStudentService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void managesStudentCrudAndCurrentCourseRegistrations() {
        DepartmentEntity department = departmentRepository.save(DepartmentEntity.builder()
                .name("Student Service")
                .type(DepartmentType.SCIENCE)
                .build());
        CourseEntity firstCourse = saveCourse(department, "STU-101");
        CourseEntity secondCourse = saveCourse(department, "STU-201");

        StudentDto created = studentService.createOne(
                studentInput(null, "Alex Student", 1, true, Set.of(firstCourse.getId())));
        assertThat(created.id()).isNotNull();
        assertThat(created.courseIds()).containsExactly(firstCourse.getId());
        assertThat(studentService.getAll()).extracting(StudentDto::id).contains(created.id());

        StudentDto registered = studentService.registerForCourse(created.id(), firstCourse.getId());
        assertThat(registered.courseIds()).containsExactly(firstCourse.getId());
        assertThat(studentService.registerForCourse(created.id(), firstCourse.getId()).courseIds())
                .containsExactly(firstCourse.getId());

        StudentDto updated = studentService.updateOne(created.id(),
                studentInput(created.id(), "Alex Morgan", 2, false, Set.of(secondCourse.getId())));
        assertThat(updated.name()).isEqualTo("Alex Morgan");
        assertThat(updated.academicYear()).isEqualTo(2);
        assertThat(updated.active()).isFalse();
        assertThat(updated.courseIds()).containsExactly(secondCourse.getId());

        StudentDto deregistered = studentService.deregisterFromCourse(created.id(), secondCourse.getId());
        assertThat(deregistered.courseIds()).isEmpty();
        assertThat(studentService.deregisterFromCourse(created.id(), secondCourse.getId()).courseIds()).isEmpty();

        studentService.registerForCourse(created.id(), firstCourse.getId());
        assertThat(courseRepository.findById(firstCourse.getId()).orElseThrow().getStudents())
                .extracting(StudentEntity::getId)
                .containsExactly(created.id());
        studentService.deleteOne(created.id());
        assertThat(studentRepository.findById(created.id())).isEmpty();
        assertThat(courseRepository.findById(firstCourse.getId()).orElseThrow().getStudents()).isEmpty();
        assertThat(courseRepository.findById(secondCourse.getId()).orElseThrow().getStudents()).isEmpty();
    }

    @Test
    void managesProfileCrudAndDeletesProfileWithStudent() {
        StudentDto student = studentService.createOne(studentInput(null, "Casey Student", 1, true, Set.of()));

        StudentProfileDto created = studentService.createProfile(
                new StudentProfileDto(null, "casey@example.test", "555-0100", student.id()));
        assertThat(studentService.getProfile(created.id())).isEqualTo(created);
        assertThat(studentService.getProfileForStudent(student.id())).isEqualTo(created);
        assertThat(studentService.getOne(student.id()).profileId()).isEqualTo(created.id());
        assertThat(studentService.getAllProfiles()).containsExactly(created);
        assertThatThrownBy(() -> studentService.createProfile(
                new StudentProfileDto(null, "duplicate@example.test", null, student.id())))
                .isInstanceOf(IllegalStateException.class);

        StudentProfileDto updated = studentService.updateProfile(created.id(),
                new StudentProfileDto(created.id(), "casey.updated@example.test", null, student.id()));
        assertThat(updated.email()).isEqualTo("casey.updated@example.test");
        assertThat(updated.phone()).isNull();
        assertThatThrownBy(() -> studentService.updateProfile(created.id(),
                new StudentProfileDto(created.id(), "casey@example.test", null, student.id() + 1)))
                .isInstanceOf(IllegalArgumentException.class);

        studentService.deleteProfile(created.id());
        assertThat(studentService.getOne(student.id()).profileId()).isNull();
        assertThat(studentProfileRepository.findById(created.id())).isEmpty();
        assertThatThrownBy(() -> studentService.getProfile(created.id()))
                .isInstanceOf(EntityNotFoundException.class);

        StudentProfileDto secondProfile = studentService.createProfile(
                new StudentProfileDto(null, "casey.second@example.test", null, student.id()));
        studentService.deleteOne(student.id());
        assertThat(studentProfileRepository.findById(secondProfile.id())).isEmpty();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void rollsBackInvalidStudentUpdate() {
        StudentEntity student = studentRepository.save(StudentEntity.builder()
                .name("Persisted Student")
                .academicYear(1)
                .active(Boolean.TRUE)
                .build());

        assertThatThrownBy(() -> studentService.updateOne(student.getId(),
                studentInput(student.getId(), "Invalid Update", 0, false, Set.of())))
                .isInstanceOf(TransactionSystemException.class)
                .hasRootCauseInstanceOf(ConstraintViolationException.class);

        StudentEntity stored = studentRepository.findById(student.getId()).orElseThrow();
        assertThat(stored.getName()).isEqualTo("Persisted Student");
        assertThat(stored.getAcademicYear()).isEqualTo(1);
        assertThat(stored.getActive()).isTrue();
    }

    private CourseEntity saveCourse(DepartmentEntity department, String code) {
        return courseRepository.save(CourseEntity.builder()
                .code(code)
                .title(code + " Foundations")
                .tuition(100.0)
                .active(Boolean.TRUE)
                .level(CourseLevel.INTRODUCTORY)
                .department(department)
                .build());
    }

    private static StudentDto studentInput(Integer id, String name, Integer academicYear,
            Boolean active, Set<Integer> courseIds) {
        return new StudentDto(id, name, academicYear, active, null, courseIds);
    }
}
