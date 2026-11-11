package com.fp.boot_demo_web.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.fp.boot_demo_web.model.dto.CourseDto;
import com.fp.boot_demo_web.model.dto.DepartmentDto;
import com.fp.boot_demo_web.model.dto.StudentDto;
import com.fp.boot_demo_web.model.enums.CourseLevel;
import com.fp.boot_demo_web.model.enums.DepartmentType;
import com.fp.boot_demo_web.repository.CourseRepository;
import com.fp.boot_demo_web.repository.DepartmentRepository;
import com.fp.boot_demo_web.repository.StudentRepository;

/** Integration test for transactions spanning both academic services. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ DepartmentService.class, StudentService.class })
@ActiveProfiles("test")
class TestAcademicServiceTransactions {

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

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void rollsBackDepartmentCourseAndRegistrationAsOneUnit() {
        AtomicReference<Integer> departmentId = new AtomicReference<>();
        AtomicReference<Integer> courseId = new AtomicReference<>();
        AtomicReference<Integer> studentId = new AtomicReference<>();
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.executeWithoutResult((TransactionStatus status) -> {
            DepartmentDto department = departmentService.createOne(
                    new DepartmentDto(null, "Atomic Department", DepartmentType.SCIENCE, Set.of()));
            CourseDto course = departmentService.createCourse(new CourseDto(null, "ATOMIC-101",
                    "Atomic Course", 100.0, true, CourseLevel.INTRODUCTORY, department.id(), Set.of()));
            StudentDto student = studentService.createOne(
                    new StudentDto(null, "Atomic Student", 1, true, null, Set.of()));
            studentService.registerForCourse(student.id(), course.id());

            entityManager.flush();
            departmentId.set(department.id());
            courseId.set(course.id());
            studentId.set(student.id());
            status.setRollbackOnly();
        });

        assertThat(departmentRepository.findById(departmentId.get())).isEmpty();
        assertThat(courseRepository.findById(courseId.get())).isEmpty();
        assertThat(studentRepository.findById(studentId.get())).isEmpty();
    }
}
