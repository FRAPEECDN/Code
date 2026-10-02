package foundation.jdbc.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * PostgreSQL repository backed by a HikariCP connection pool.
 *
 * <p>Closing this repository closes its {@link HikariDataSource} and releases pooled connections.
 */
public final class HikariPostgresProjectRepository extends JdbcProjectRepository {
     /**
      * Creates a pooled repository for the supplied PostgreSQL connection.
      *
      * @param jdbcUrl PostgreSQL JDBC URL
      * @param username database username
      * @param password database password
      */
    public HikariPostgresProjectRepository(String jdbcUrl, String username, String password) {
        super(createDataSource(jdbcUrl, username, password));
    }

    private static HikariDataSource createDataSource(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("foundation-jdbc-postgres");
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }
}