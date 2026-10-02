package foundation.jdbc.repository;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * JDBC project repository backed by PostgreSQL without a connection pool.
 * Use {@link HikariPostgresProjectRepository} when pooled connections are desired.
 */
public final class PostgresProjectRepository extends JdbcProjectRepository {
     /**
      * Creates an unpooled repository; each request obtains a connection from {@link DriverManager}.
      *
      * @param jdbcUrl PostgreSQL JDBC URL
      * @param username database username
      * @param password database password
      */
    public PostgresProjectRepository(String jdbcUrl, String username, String password) {
        super(new DriverManagerDataSource(jdbcUrl, username, password));
    }

    private record DriverManagerDataSource(String jdbcUrl, String username, String password) implements DataSource {
        @Override
        public java.sql.Connection getConnection() throws SQLException {
            return DriverManager.getConnection(jdbcUrl, username, password);
        }

        @Override
        public java.sql.Connection getConnection(String user, String pass) throws SQLException {
            return DriverManager.getConnection(jdbcUrl, user, pass);
        }

        @Override
        public java.io.PrintWriter getLogWriter() {
            return null;
        }

        @Override
        public void setLogWriter(java.io.PrintWriter out) {}

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            DriverManager.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return DriverManager.getLoginTimeout();
        }

        @Override
        public java.util.logging.Logger getParentLogger() {
            return java.util.logging.Logger.getLogger("java.sql");
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (iface.isInstance(this)) {
                return iface.cast(this);
            }
            throw new SQLException("not a wrapper for " + iface.getName());
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return iface.isInstance(this);
        }
    }
}