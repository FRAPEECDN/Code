package com.fp.boot_demo_web.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.fp.boot_demo_web.model.entities.CourseEntity;
import com.fp.boot_demo_web.model.entities.DepartmentEntity;
import com.fp.boot_demo_web.model.enums.CourseLevel;
import com.fp.boot_demo_web.model.enums.DepartmentType;

/** Repository-slice tests for atomic multi-repository transactions. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TestAcademicRepositoryTransactions {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void rollsBackDepartmentAndCourseTogether() {
        String departmentName = "Rolled Back Department";
        AtomicReference<Integer> departmentId = new AtomicReference<>();
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.executeWithoutResult((TransactionStatus status) -> {
            DepartmentEntity department = departmentRepository.save(DepartmentEntity.builder()
                    .name(departmentName)
                    .type(DepartmentType.SCIENCE)
                    .build());
            CourseEntity course = CourseEntity.builder()
                    .code("ROLLBACK-101")
                    .title("Transaction Rollback")
                    .tuition(100.0)
                    .active(Boolean.TRUE)
                    .level(CourseLevel.INTRODUCTORY)
                    .department(department)
                    .build();
            courseRepository.save(course);
            entityManager.flush();

            departmentId.set(department.getId());
            status.setRollbackOnly();
        });

        assertThat(departmentRepository.findByName(departmentName)).isEmpty();
        assertThat(courseRepository.findByDepartment_Id(departmentId.get())).isEmpty();
    }
}
