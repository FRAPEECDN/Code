package com.fp.boot_demo_web.repository;

import org.springframework.data.repository.CrudRepository;

import com.fp.boot_demo_web.model.entities.SimpleEntity;

/**
 * Spring Data repository for CRUD access to {@link SimpleEntity} records.
 */
public interface SimpleRepository extends CrudRepository<SimpleEntity, Integer> {

}
