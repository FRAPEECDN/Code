package com.fp.boot_demo_web.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.entities.CourseEntity;
import com.fp.boot_demo_web.model.entities.DepartmentEntity;
import com.fp.boot_demo_web.model.entities.StudentEntity;
import com.fp.boot_demo_web.model.entities.StudentProfileEntity;
import com.fp.boot_demo_web.model.enums.CourseLevel;
import com.fp.boot_demo_web.model.enums.DepartmentType;

/** Repository-slice tests for the full demo's scalar fields and relationships. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TestAcademicRepositories {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void persistsWrapperScalarsAndBothEnumStrategies() {
        DepartmentEntity department = departmentRepository.save(DepartmentEntity.builder()
                .name("Science")
                .type(DepartmentType.SCIENCE)
                .build());
        CourseEntity course = courseRepository.save(course(department, "SCI-101", CourseLevel.ADVANCED));
        StudentEntity student = studentRepository.save(StudentEntity.builder()
                .name("Alex Student")
                .academicYear(2)
                .active(Boolean.TRUE)
                .build());

        entityManager.flush();
        entityManager.clear();

        assertThat(jdbcTemplate.queryForObject(
                "SELECT department_type FROM department WHERE id = ?", String.class, department.getId()))
                .isEqualTo("SCIENCE");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT course_level FROM course WHERE id = ?", Integer.class, course.getId()))
                .isEqualTo(CourseLevel.ADVANCED.ordinal());
        assertThat(jdbcTemplate.queryForObject(
                "SELECT tuition FROM course WHERE id = ?", Double.class, course.getId()))
                .isEqualTo(125.75);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT active FROM course WHERE id = ?", Boolean.class, course.getId()))
                .isTrue();
        assertThat(studentRepository.findById(student.getId()).orElseThrow())
                .satisfies(stored -> {
                    assertThat(stored.getAcademicYear()).isEqualTo(2);
                    assertThat(stored.getActive()).isTrue();
                });
        assertThat(departmentRepository.findByName("Science")).isPresent();
    }

    @Test
    void persistsOneToManyAndManyToOneDepartmentCourseLinks() {
        DepartmentEntity department = DepartmentEntity.builder()
                .name("Arts")
                .type(DepartmentType.ARTS)
                .build();
        CourseEntity course = course(department, "ART-101", CourseLevel.INTRODUCTORY);
        department.getCourses().add(course);

        DepartmentEntity savedDepartment = departmentRepository.save(department);
        entityManager.flush();
        entityManager.clear();

        List<CourseEntity> departmentCourses = courseRepository.findByDepartment_Id(savedDepartment.getId());
        assertThat(departmentCourses).hasSize(1);
        assertThat(departmentCourses.get(0).getDepartment().getId()).isEqualTo(savedDepartment.getId());
        assertThat(departmentRepository.findById(savedDepartment.getId()).orElseThrow().getCourses())
                .extracting(CourseEntity::getCode)
                .containsExactly("ART-101");
    }

    @Test
    void persistsOneToOneStudentProfileLink() {
        StudentEntity student = student("Casey Student");
        StudentProfileEntity profile = StudentProfileEntity.builder()
                .email("casey@example.test")
                .phone("555-0100")
                .student(student)
                .build();
        student.setProfile(profile);

        StudentEntity savedStudent = studentRepository.save(student);
        entityManager.flush();
        entityManager.clear();

        assertThat(studentProfileRepository.findByStudent_Id(savedStudent.getId()))
                .get()
                .satisfies(savedProfile -> {
                    assertThat(savedProfile.getEmail()).isEqualTo("casey@example.test");
                    assertThat(savedProfile.getStudent().getId()).isEqualTo(savedStudent.getId());
                });
    }

    @Test
    void eachAcademicRepositorySupportsCrudOperations() {
        DepartmentEntity department = departmentRepository.save(DepartmentEntity.builder()
                .name("Repository CRUD")
                .type(DepartmentType.SCIENCE)
                .build());
        CourseEntity course = courseRepository.save(course(department, "CRUD-101", CourseLevel.INTRODUCTORY));
        StudentEntity student = studentRepository.save(student("Repository Student"));
        StudentProfileEntity profile = studentProfileRepository.save(StudentProfileEntity.builder()
                .email("repository@example.test")
                .student(student)
                .build());

        entityManager.flush();
        entityManager.clear();

        assertThat(departmentRepository.findById(department.getId())).isPresent();
        assertThat(courseRepository.findById(course.getId())).isPresent();
        assertThat(studentRepository.findById(student.getId())).isPresent();
        assertThat(studentProfileRepository.findById(profile.getId())).isPresent();

        studentProfileRepository.deleteById(profile.getId());
        courseRepository.deleteById(course.getId());
        studentRepository.deleteById(student.getId());
        departmentRepository.deleteById(department.getId());

        entityManager.flush();
        entityManager.clear();

        assertThat(departmentRepository.findById(department.getId())).isEmpty();
        assertThat(courseRepository.findById(course.getId())).isEmpty();
        assertThat(studentRepository.findById(student.getId())).isEmpty();
        assertThat(studentProfileRepository.findById(profile.getId())).isEmpty();
    }

    @Test
    void persistsManyToManyStudentCourseLinksAndQueriesByCourse() {
        DepartmentEntity department = departmentRepository.save(DepartmentEntity.builder()
                .name("Business")
                .type(DepartmentType.BUSINESS)
                .build());
        CourseEntity course = courseRepository.save(course(department, "BUS-101", CourseLevel.INTERMEDIATE));
        StudentEntity student = student("Jordan Student");
        student.getCourses().add(course);

        StudentEntity savedStudent = studentRepository.save(student);
        entityManager.flush();
        entityManager.clear();

        assertThat(studentRepository.findDistinctByCourses_Id(course.getId()))
                .extracting(StudentEntity::getId)
                .containsExactly(savedStudent.getId());
        assertThat(courseRepository.findById(course.getId()).orElseThrow().getStudents())
                .extracting(StudentEntity::getName)
                .containsExactly("Jordan Student");
    }

    private CourseEntity course(DepartmentEntity department, String code, CourseLevel level) {
        return CourseEntity.builder()
                .code(code)
                .title(code + " Foundations")
                .tuition(125.75)
                .active(Boolean.TRUE)
                .level(level)
                .department(department)
                .build();
    }

    private StudentEntity student(String name) {
        return StudentEntity.builder()
                .name(name)
                .academicYear(1)
                .active(Boolean.TRUE)
                .build();
    }
}
