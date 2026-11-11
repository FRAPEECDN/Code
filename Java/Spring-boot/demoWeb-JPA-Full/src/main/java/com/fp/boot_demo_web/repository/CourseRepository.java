package com.fp.boot_demo_web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fp.boot_demo_web.model.entities.CourseEntity;

/** Repository for courses and their department relationship. */
public interface CourseRepository extends JpaRepository<CourseEntity, Integer> {

    /** Finds courses assigned to a department. */
    List<CourseEntity> findByDepartment_Id(Integer departmentId);
}
