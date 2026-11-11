package com.fp.boot_demo_web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fp.boot_demo_web.model.entities.DepartmentEntity;

/** Repository for department records. */
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Integer> {

    /** Finds the department with the given unique name. */
    Optional<DepartmentEntity> findByName(String name);
}
