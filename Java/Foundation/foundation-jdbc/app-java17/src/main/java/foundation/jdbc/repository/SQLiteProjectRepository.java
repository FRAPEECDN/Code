package foundation.jdbc.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.sqlite.SQLiteDataSource;

/** JDBC project repository backed by SQLite. */
public final class SQLiteProjectRepository extends JdbcProjectRepository {
    /** Creates a repository using {@code foundation-jdbc.db} in the current working directory. */
    public SQLiteProjectRepository() {
        this("jdbc:sqlite:foundation-jdbc.db");
    }

    /**
     * Creates a SQLite repository using the supplied JDBC URL.
     *
     * @param jdbcUrl URL such as {@code jdbc:sqlite:path/to/projects.db}
     */
    public SQLiteProjectRepository(String jdbcUrl) {
        super(dataSource(jdbcUrl));
    }

    @Override
    protected String ownerIdSqlType() {
        return "VARCHAR(36)";
    }

    @Override
    protected String projectIdColumnDefinition() {
        return "INTEGER PRIMARY KEY AUTOINCREMENT";
    }

    @Override
    protected void bindOwnerId(PreparedStatement statement, int index, UUID ownerId) throws SQLException {
        statement.setString(index, ownerId.toString());
    }

    @Override
    protected UUID readOwnerId(ResultSet result, String column) throws SQLException {
        return UUID.fromString(result.getString(column));
    }

    private static DataSource dataSource(String jdbcUrl) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(jdbcUrl);
        return dataSource;
    }
}