package com.fp.boot_demo_web.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fp.boot_demo_web.model.entities.SimpleEntity;

/**
 * Performs CRUD operations for {@link SimpleEntity} records with SQL.
 */
@Repository
public class SimpleRepository {

    private static final RowMapper<SimpleEntity> ROW_MAPPER = (resultSet, rowNumber) ->
            new SimpleEntity(resultSet.getInt("id"), resultSet.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates the repository with its JDBC operations delegate.
     *
     * @param jdbcTemplate template used to execute parameterized SQL
     */
    public SimpleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieves all records ordered by their identifiers.
     *
     * @return all stored records
     */
    public List<SimpleEntity> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM simple ORDER BY id", ROW_MAPPER);
    }

    /**
     * Retrieves a record by its identifier.
     *
     * @param id identifier of the requested record
     * @return the matching record, or an empty optional if it does not exist
     */
    public Optional<SimpleEntity> findById(int id) {
        return jdbcTemplate.query("SELECT id, name FROM simple WHERE id = ?", ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    /**
     * Inserts a record with id {@code 0}, or updates the record with a non-zero id.
     *
     * @param entity record values to persist
     * @return the persisted record, including its generated id after an insert
     * @throws IllegalArgumentException if an update targets an id not in the database
     * @throws NullPointerException if {@code entity} is {@code null}
     */
    public SimpleEntity save(SimpleEntity entity) {
        if (entity.getId() == 0) {
            return insert(entity);
        }

        int updatedRows = jdbcTemplate.update(
                "UPDATE simple SET name = ? WHERE id = ?",
                entity.getName(),
                entity.getId());
        if (updatedRows == 0) {
            throw new IllegalArgumentException("No simple record exists with id " + entity.getId());
        }
        return entity;
    }

    /**
     * Deletes the record identified by the supplied row model.
     *
     * @param entity record to delete
     * @throws NullPointerException if {@code entity} is {@code null}
     */
    public void delete(SimpleEntity entity) {
        jdbcTemplate.update("DELETE FROM simple WHERE id = ?", entity.getId());
    }

    private SimpleEntity insert(SimpleEntity entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO simple (name) VALUES (?)",
                    new String[] { "id" });
            statement.setString(1, entity.getName());
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new IllegalStateException("The database did not return an id for the inserted record");
        }
        return new SimpleEntity(generatedId.intValue(), entity.getName());
    }

}
