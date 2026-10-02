package foundation.jdbc.repository;

/** Unchecked wrapper for JDBC persistence failures. */
public final class JdbcRepositoryException extends RuntimeException {
    /**
     * Creates a persistence exception with an operation-specific message and original cause.
     *
     * @param message operation context
     * @param cause underlying JDBC or resource error
     */
    public JdbcRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}