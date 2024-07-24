package org.swiftcache.datasource;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.util.TestDatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for the {@link DataSource} class.
 *
 * Tests the core functionalities of fetching, storing, and deleting data from the database using the {@link DataSource}.
 * Ensures that the data source interacts correctly with the database and that SQL operations are performed as expected.
 *
 * Uses JUnit for test framework and {@link TestDatabaseUtil} for setting up and cleaning the database.
 *
 * @see DataSource
 * @see TestDatabaseUtil
 */
public class DataSourceTest {

    private DataSource<String, Integer> dataSource;
    private Connection connection;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    /**
     * Sets up the test environment before each test method.
     * Initializes the database connection and {@link DataSource}.
     * Clears the test table to ensure a clean state before each test.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        // Ensure the table is clean before starting tests
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Inserts test data into the database.
     *
     * @param key the key for the test data.
     * @param value the value for the test data.
     * @throws SQLException if a database access error occurs.
     */
    private void insertTestData(String key, int value) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, key);
            statement.setInt(2, value);
            statement.executeUpdate();
        }
    }

    /**
     * Tests the {@link DataSource#fetch} method.
     * Verifies that the data source correctly retrieves data from the database.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testFetch() throws SQLException {
        insertTestData("key1", 42);

        Integer result = dataSource.fetch("key1", SELECT_SQL);
        assertEquals(Integer.valueOf(42), result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link DataSource#store} method.
     * Verifies that the data source correctly stores data into the database.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testStore() throws SQLException {
        dataSource.store("key2", 43, INSERT_SQL);

        Integer result = dataSource.fetch("key2", SELECT_SQL);
        assertEquals(Integer.valueOf(43), result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link DataSource#delete} method.
     * Verifies that the data source correctly deletes data from the database.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testDelete() throws SQLException {
        insertTestData("key3", 44);

        dataSource.delete("key3", DELETE_SQL);

        Integer result = dataSource.fetch("key3", SELECT_SQL);
        assertNull(result);
        TestDatabaseUtil.clearTable(connection);
    }
}
