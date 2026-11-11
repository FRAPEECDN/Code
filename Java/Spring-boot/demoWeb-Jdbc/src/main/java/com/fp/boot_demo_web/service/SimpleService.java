package com.fp.boot_demo_web.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.fp.boot_demo_web.exceptions.EntityNotFoundException;
import com.fp.boot_demo_web.exceptions.GeneralServiceException;
import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.fp.boot_demo_web.model.entities.SimpleEntity;
import com.fp.boot_demo_web.repository.SimpleRepository;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements record operations and converts between API DTOs and JDBC row models.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleService {

    private final SimpleRepository repository;

    /**
     * Retrieves all stored records as API DTOs.
     *
     * @return all records
     */
    public @Nonnull List<SimpleDto> getAll() {
        log.info("Retrievig all items from the database");
        Iterable<SimpleEntity> result = repository.findAll();
        Stream<SimpleEntity> stream = StreamSupport.stream(result.spliterator(), false);
        return stream.map(e -> new SimpleDto(e.getId(), e.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves one record by its identifier.
     *
     * @param id identifier of the requested record
     * @return the matching record
     * @throws EntityNotFoundException if no record has the requested identifier
     */
    public @Nullable SimpleDto getOne(int id) {
        log.info("Retrieving item from the database");
        Optional<SimpleEntity> result = repository.findById(id);
        if (result.isPresent()) {
            return new SimpleDto(result.get().getId(), result.get().getName());
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Persists a new record.
     *
     * @param input record data to persist
     * @return the saved record
     * @throws NullPointerException if {@code input} is {@code null}
     * @throws GeneralServiceException if the repository rejects the new record
     */
    public @Nonnull SimpleDto createOne(@Nullable SimpleDto input) {
        log.info("Adding new item to the database");
        if (input == null) {
            throw new NullPointerException();
        }
        SimpleEntity newEntity = new SimpleEntity(input.id(), input.name());
        SimpleEntity result;
        try {
            result = repository.save(newEntity);
        } catch (IllegalArgumentException ex) {
            throw new GeneralServiceException();
        }
        return new SimpleDto(result.getId(), result.getName());
    }

    /**
     * Replaces a stored record with the supplied DTO data.
     *
     * @param id identifier of the record to replace
     * @param newValue replacement record data
     * @return the saved record
     * @throws NullPointerException if {@code newValue} is {@code null}
     * @throws EntityNotFoundException if no record has the requested identifier
     * @throws GeneralServiceException if the repository rejects the updated record
     */
    public SimpleDto updateOne(int id, @Nullable SimpleDto newValue) {
        log.info("Changing an item with id from database, with changed values");
        if (newValue == null) {
            throw new NullPointerException();
        }
        Optional<SimpleEntity> lookup = repository.findById(id);
        if (!lookup.isPresent()) {
            throw new EntityNotFoundException();
        }
        SimpleEntity changedEntity = new SimpleEntity(newValue.id(), newValue.name());
        SimpleEntity result;
        try {
            result = repository.save(changedEntity);
        } catch (IllegalArgumentException ex) {
            throw new GeneralServiceException();
        }
        return new SimpleDto(result.getId(), result.getName());
    }

    /**
     * Deletes a stored record.
     *
     * @param id identifier of the record to delete
     * @throws EntityNotFoundException if no record has the requested identifier
     */
    public void deleteOne(int id) {
        log.info("Removing an item from the database");
        Optional<SimpleEntity> lookup = repository.findById(id);
        if (lookup.isPresent()) {
            repository.delete(lookup.get());
        } else {
            throw new EntityNotFoundException();
        }
    }

}
