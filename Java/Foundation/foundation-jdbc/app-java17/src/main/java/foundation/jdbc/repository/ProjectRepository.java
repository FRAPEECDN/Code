package foundation.jdbc.repository;

import java.util.List;
import java.util.Optional;

import foundation.jdbc.model.Project;

/**
 * JDBC CRUD operations for projects and their lifecycle history.
 * Implementations own their data source and should be closed when no longer needed.
 */
public interface ProjectRepository extends AutoCloseable {
    /**
     * Persists a project and its current status history.
     *
     * @param project project to create
     * @return generated database id
     */
    long create(Project project);

    /**
     * Finds one project by its generated id.
     *
     * @param id generated project id
     * @return the project when found, otherwise an empty optional
     */
    Optional<Project> findById(long id);

    /**
     * Returns all projects ordered by their generated ids.
     *
     * @return immutable list of projects
     */
    List<Project> findAll();

    /**
     * Replaces a project's stored details and status history.
     *
     * @param id generated id of the row to replace
     * @param project replacement project state
     * @return {@code true} if a row was updated, otherwise {@code false}
     */
    boolean update(long id, Project project);

    /**
     * Deletes a project and its dependent status history.
     *
     * @param id generated project id
     * @return {@code true} if a row was deleted, otherwise {@code false}
     */
    boolean deleteById(long id);

    /** Releases repository resources; repositories without owned resources need no action. */
    @Override
    default void close() {}
}