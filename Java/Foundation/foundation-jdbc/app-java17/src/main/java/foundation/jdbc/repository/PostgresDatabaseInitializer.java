package foundation.jdbc.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/** Creates the application database through a PostgreSQL maintenance database connection. */
public final class PostgresDatabaseInitializer {
    private static final long DATABASE_CREATION_LOCK = 7231400123456789L;

    private PostgresDatabaseInitializer() {}

    /**
     * Creates the named database if it is absent. The supplied URL must connect to an existing
     * maintenance database, and the user must have {@code CREATEDB} permission.
     *
     * <p>An advisory lock serializes multiple application processes that start at the same time.
     *
     * @param adminJdbcUrl JDBC URL for an existing database such as {@code postgres}
     * @param username database user
     * @param password database password
     * @param databaseName new database name; limited to letters, digits, underscores, and hyphens
     * @throws IllegalArgumentException if the database name is not a supported identifier
     * @throws JdbcRepositoryException if PostgreSQL cannot check or create the database
     */
    public static void ensureDatabaseExists(String adminJdbcUrl, String username, String password, String databaseName) {
        Objects.requireNonNull(adminJdbcUrl, "adminJdbcUrl");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");
        if (databaseName == null || !databaseName.matches("[A-Za-z][A-Za-z0-9_-]*")) {
            throw new IllegalArgumentException("databaseName must start with a letter and contain only letters, digits, _ or -");
        }

        try (Connection connection = DriverManager.getConnection(adminJdbcUrl, username, password)) {
            // The session lock prevents two app versions from racing between the existence check and CREATE DATABASE.
            try (PreparedStatement lock = connection.prepareStatement("SELECT pg_advisory_lock(?)")) {
                lock.setLong(1, DATABASE_CREATION_LOCK);
                lock.execute();
            }
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT 1 FROM pg_database WHERE datname = ?")) {
                query.setString(1, databaseName);
                try (ResultSet result = query.executeQuery()) {
                    if (result.next()) {
                        return;
                    }
                }
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE DATABASE \"" + databaseName + "\"");
            } catch (SQLException exception) {
                if (!"42P04".equals(exception.getSQLState())) {
                    throw exception;
                }
            }
        } catch (SQLException exception) {
            throw new JdbcRepositoryException("Failed to create or check PostgreSQL database " + databaseName, exception);
        }
    }
}