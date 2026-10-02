package foundation.jdbc.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import foundation.jdbc.model.Project;
import foundation.jdbc.model.ProjectOwner;
import foundation.jdbc.model.ProjectStatus;

/**
 * Shared JDBC implementation used by the H2, SQLite, and PostgreSQL repositories.
 *
 * <p>Database-specific subclasses override the identity-column and owner-UUID mapping hooks. Writes
 * use transactions so project rows and lifecycle history are persisted atomically.
 */
public class JdbcProjectRepository implements ProjectRepository {
    private final DataSource dataSource;

    /**
     * Creates the repository, validates its data source, and creates missing tables.
     *
     * @param dataSource database connections used by this repository
     */
    protected JdbcProjectRepository(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        initializeSchema();
    }

    /** Closes the data source when it owns a closeable resource, such as a Hikari pool. */
    @Override
    public void close() {
        if (dataSource instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception exception) {
                throw new JdbcRepositoryException("Failed to close project data source", exception);
            }
        }
    }

    /** Creates a project and its initial lifecycle history in one transaction. */
    @Override
    public long create(Project project) {
        Objects.requireNonNull(project, "project");
        return inTransaction(connection -> {
            ensureOwner(connection, project.getProjectOwner().getId());
            String sql = "INSERT INTO projects (owner_id, project_name, sponsor, budget, start_date, deadline, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            long id;
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bindProject(statement, project, 1);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("database did not return a project id");
                    }
                    id = keys.getLong(1);
                }
            }
            insertHistory(connection, id, project.getStatusHistory());
            return id;
        });
    }

    /** Reads a project and reconstructs its lifecycle by replaying the stored statuses. */
    @Override
    public Optional<Project> findById(long id) {
        String sql = "SELECT p.id, p.owner_id, p.project_name, p.sponsor, p.budget, p.start_date, p.deadline, p.status "
                + "FROM projects p WHERE p.id = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(readProject(connection, result, new HashMap<>())) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw databaseFailure("read project", exception);
        }
    }

    /** Reads all projects, reusing owner objects so projects share the same in-memory owner instance. */
    @Override
    public List<Project> findAll() {
        String sql = "SELECT p.id, p.owner_id, p.project_name, p.sponsor, p.budget, p.start_date, p.deadline, p.status "
                + "FROM projects p ORDER BY p.id";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            List<Project> projects = new ArrayList<>();
            Map<UUID, ProjectOwner> owners = new HashMap<>();
            while (result.next()) {
                projects.add(readProject(connection, result, owners));
            }
            return List.copyOf(projects);
        } catch (SQLException exception) {
            throw databaseFailure("list projects", exception);
        }
    }

    /** Replaces the project row and all associated status-history rows atomically. */
    @Override
    public boolean update(long id, Project project) {
        Objects.requireNonNull(project, "project");
        return inTransaction(connection -> {
            ensureOwner(connection, project.getProjectOwner().getId());
            String sql = "UPDATE projects SET owner_id = ?, project_name = ?, sponsor = ?, budget = ?, "
                    + "start_date = ?, deadline = ?, status = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindProject(statement, project, 1);
                statement.setLong(8, id);
                if (statement.executeUpdate() == 0) {
                    return false;
                }
            }
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM project_status_history WHERE project_id = ?")) {
                delete.setLong(1, id);
                delete.executeUpdate();
            }
            insertHistory(connection, id, project.getStatusHistory());
            return true;
        });
    }

    /** Deletes a project; the database foreign key cascades to its status history. */
    @Override
    public boolean deleteById(long id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM projects WHERE id = ?")) {
                statement.setLong(1, id);
                boolean deleted = statement.executeUpdate() != 0;
                connection.commit();
                return deleted;
            } catch (SQLException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw databaseFailure("delete project", exception);
        }
    }

    private void initializeSchema() {
        // These hooks keep the shared schema readable while allowing backend-specific ID/UUID types.
        String[] statements = {
            "CREATE TABLE IF NOT EXISTS project_owners (id " + ownerIdSqlType() + " PRIMARY KEY)",
            "CREATE TABLE IF NOT EXISTS projects ("
                    + "id " + projectIdColumnDefinition() + ", "
                    + "owner_id " + ownerIdSqlType() + " NOT NULL, project_name VARCHAR(255) NOT NULL, sponsor VARCHAR(255) NOT NULL, "
                    + "budget DOUBLE PRECISION NOT NULL, start_date DATE NOT NULL, deadline DATE NOT NULL, "
                    + "status VARCHAR(32) NOT NULL, "
                    + "CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES project_owners(id))",
            "CREATE TABLE IF NOT EXISTS project_status_history ("
                    + "project_id BIGINT NOT NULL, status VARCHAR(32) NOT NULL, reached_on DATE NOT NULL, "
                    + "PRIMARY KEY (project_id, status), "
                    + "CONSTRAINT fk_status_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE)"
        };
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        } catch (SQLException exception) {
            throw databaseFailure("initialize project schema", exception);
        }
    }

    private void ensureOwner(Connection connection, UUID ownerId) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM project_owners WHERE id = ?")) {
            bindOwnerId(query, 1, ownerId);
            try (ResultSet result = query.executeQuery()) {
                if (result.next()) {
                    return;
                }
            }
        }
        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO project_owners (id) VALUES (?)")) {
            bindOwnerId(insert, 1, ownerId);
            insert.executeUpdate();
        }
    }

    private void bindProject(PreparedStatement statement, Project project, int startIndex) throws SQLException {
        bindOwnerId(statement, startIndex, project.getProjectOwner().getId());
        statement.setString(startIndex + 1, project.getProjectName());
        statement.setString(startIndex + 2, project.getSponsor());
        statement.setDouble(startIndex + 3, project.getBudget());
        statement.setDate(startIndex + 4, Date.valueOf(project.getStartDate()));
        statement.setDate(startIndex + 5, Date.valueOf(project.getDeadline()));
        statement.setString(startIndex + 6, project.getStatus().name());
    }

    private static void insertHistory(Connection connection, long projectId, Map<ProjectStatus, LocalDate> history)
            throws SQLException {
        String sql = "INSERT INTO project_status_history (project_id, status, reached_on) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Map.Entry<ProjectStatus, LocalDate> entry : history.entrySet()) {
                statement.setLong(1, projectId);
                statement.setString(2, entry.getKey().name());
                statement.setDate(3, Date.valueOf(entry.getValue()));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Project readProject(
            Connection connection, ResultSet result, Map<UUID, ProjectOwner> owners) throws SQLException {
        long id = result.getLong("id");
        UUID ownerId = readOwnerId(result, "owner_id");
        ProjectOwner owner = owners.computeIfAbsent(ownerId, ProjectOwner::new);
        EnumMap<ProjectStatus, LocalDate> history = new EnumMap<>(ProjectStatus.class);
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT status, reached_on FROM project_status_history WHERE project_id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    history.put(ProjectStatus.valueOf(rows.getString("status")), rows.getDate("reached_on").toLocalDate());
                }
            }
        }
        Project project = Project.builder()
                .projectOwner(owner)
                .projectName(result.getString("project_name"))
                .sponsor(result.getString("sponsor"))
                .budget(result.getDouble("budget"))
                .startDate(result.getDate("start_date").toLocalDate())
                .deadline(result.getDate("deadline").toLocalDate())
                .registeredOn(history.get(ProjectStatus.REGISTERED))
                .build();
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status != ProjectStatus.REGISTERED && history.containsKey(status)) {
                project.transitionTo(status, history.get(status));
            }
        }
        if (project.getStatus().name().equals(result.getString("status"))) {
            return project;
        }
        throw new SQLException("project status does not match its lifecycle history");
    }

    /**
     * Returns the SQL type used for owner UUID columns in this database.
     *
     * @return SQL column type name
     */
    protected String ownerIdSqlType() {
        return "UUID";
    }

    /**
     * Returns the complete primary-key column definition for the project table.
     *
     * @return SQL column definition, including primary-key constraints
     */
    protected String projectIdColumnDefinition() {
        return "BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY";
    }

    /**
     * Binds an owner UUID using this database's expected representation.
     *
     * @param statement statement receiving the parameter
     * @param index one-based parameter index
     * @param ownerId owner UUID to bind
     * @throws SQLException if the parameter cannot be bound
     */
    protected void bindOwnerId(PreparedStatement statement, int index, UUID ownerId) throws SQLException {
        statement.setObject(index, ownerId);
    }

    /**
     * Reads and converts an owner UUID from this database's representation.
     *
     * @param result current query result
     * @param column result column containing the owner UUID
     * @return parsed owner UUID
     * @throws SQLException if the column cannot be read
     */
    protected UUID readOwnerId(ResultSet result, String column) throws SQLException {
        Object value = result.getObject(column);
        return value instanceof UUID uuid ? uuid : UUID.fromString(Objects.toString(value));
    }

    private <T> T inTransaction(SqlWork<T> work) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (Exception exception) {
                // Preserve the original failure if rollback also fails.
                rollback(connection, exception);
                if (exception instanceof SQLException sqlException) {
                    throw databaseFailure("write project", sqlException);
                }
                if (exception instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw new JdbcRepositoryException("write project failed", exception);
            }
        } catch (SQLException exception) {
            throw databaseFailure("write project", exception);
        }
    }

    private static void rollback(Connection connection, Exception exception) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            exception.addSuppressed(rollbackException);
        }
    }

    private static JdbcRepositoryException databaseFailure(String operation, SQLException exception) {
        return new JdbcRepositoryException("Failed to " + operation, exception);
    }

    @FunctionalInterface
    private interface SqlWork<T> {
        T execute(Connection connection) throws Exception;
    }
}