package com.fp.boot_demo_web.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fp.boot_demo_web.model.entities.SimpleEntity;

/**
 * Spring Data MongoDB repository for CRUD access to {@link SimpleEntity} documents.
 */
public interface SimpleRepository extends MongoRepository<SimpleEntity, String> {

}
