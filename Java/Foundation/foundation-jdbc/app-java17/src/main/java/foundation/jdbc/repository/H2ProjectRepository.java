package foundation.jdbc.repository;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/** JDBC project repository backed by H2, useful for fast local tests. */
public final class H2ProjectRepository extends JdbcProjectRepository {
    /** Creates an in-memory H2 database that remains available for the JVM lifetime. */
    public H2ProjectRepository() {
        this("jdbc:h2:mem:projects;DB_CLOSE_DELAY=-1", "sa", "");
    }

    /**
     * Creates an H2-backed repository using the supplied connection credentials.
     *
     * @param jdbcUrl H2 JDBC URL
     * @param username database username
     * @param password database password
     */
    public H2ProjectRepository(String jdbcUrl, String username, String password) {
        super(dataSource(jdbcUrl, username, password));
    }

    private static DataSource dataSource(String jdbcUrl, String username, String password) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}