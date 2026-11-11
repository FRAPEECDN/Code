package com.fp.boot_demo_web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fp.boot_demo_web.model.entities.StudentProfileEntity;

/** Repository for student profile records. */
public interface StudentProfileRepository extends JpaRepository<StudentProfileEntity, Integer> {

    /** Finds a profile by its owning student's ID. */
    Optional<StudentProfileEntity> findByStudent_Id(Integer studentId);
}
