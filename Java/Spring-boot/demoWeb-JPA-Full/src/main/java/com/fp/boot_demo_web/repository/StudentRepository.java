package com.fp.boot_demo_web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fp.boot_demo_web.model.entities.StudentEntity;

/** Repository for students and their enrolled courses. */
public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {

    /** Finds students enrolled in the specified course. */
    List<StudentEntity> findDistinctByCourses_Id(Integer courseId);
}
