package org.swiftcache.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing a test database connection.
 *
 * Provides static methods to obtain a database connection, create the
 * necessary test table, and clear the test table. This utility uses an in-memory
 * H2 database for testing purposes.
 *
 * @see <a href="https://www.h2database.com/html/main.html">H2 Database</a>
 */
public class TestDatabaseUtil {

    // JDBC URL for an in-memory H2 database
    private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    // Default user for the H2 database
    private static final String USER = "sa";
    // Default password for the H2 database (empty)
    private static final String PASSWORD = "";
    // SQL statement to create the test table
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS test_table (testKey VARCHAR(255) PRIMARY KEY, testValue INTEGER)";
    // SQL statement to clear the test table
    private static final String CLEAR_SQL = "DELETE FROM test_table";

    /**
     * Obtains a connection to the in-memory H2 database and creates the test table.
     *
     * @return a {@link Connection} object for the in-memory H2 database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        createTable(connection);
        return connection;
    }

    /**
     * Creates the test table in the database.
     *
     * @param connection the database connection to use for creating the table
     * @throws SQLException if a database access error occurs
     */
    public static void createTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_TABLE_SQL);
    }

    /**
     * Clears all data from the test table.
     *
     * @param connection the database connection to use for clearing the table
     * @throws SQLException if a database access error occurs
     */
    public static void clearTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CLEAR_SQL);
    }
}
