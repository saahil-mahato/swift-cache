package org.swiftcache.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing a test database.
 * <p>
 * This class provides methods to obtain a connection to an in-memory database, create the necessary
 * tables, and clear data from the tables. It is designed to support testing environments that require
 * a temporary database.
 * </p>
 */
public class TestDatabaseUtil {

    private static final String JDBC_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS test_table (testKey VARCHAR(255) PRIMARY KEY, testValue INTEGER)";
    private static final String CLEAR_SQL = "DELETE FROM test_table";

    /**
     * Obtains a connection to the in-memory test database.
     * <p>
     * This method establishes a connection to an H2 in-memory database configured to operate in PostgreSQL mode.
     * It also ensures that the necessary table is created if it does not already exist.
     * </p>
     *
     * @return a {@link Connection} object to the in-memory test database
     * @throws SQLException if there is an error obtaining the database connection or creating the table
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        createTable(connection);
        return connection;
    }

    /**
     * Creates the test table in the database if it does not already exist.
     * <p>
     * This method executes a SQL statement to create a table named {@code test_table} with columns for
     * {@code testKey} and {@code testValue}. The table is configured to store key-value pairs where the key
     * is a string and the value is an integer.
     * </p>
     *
     * @param connection the {@link Connection} object used to execute the SQL statement
     * @throws SQLException if there is an error executing the SQL statement
     */
    public static void createTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_TABLE_SQL);
    }

    /**
     * Clears all data from the test table.
     * <p>
     * This method executes a SQL statement to delete all rows from the {@code test_table}. It is useful
     * for ensuring that the test table is empty before each test case is run.
     * </p>
     *
     * @param connection the {@link Connection} object used to execute the SQL statement
     * @throws SQLException if there is an error executing the SQL statement
     */
    public static void clearTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CLEAR_SQL);
    }
}
