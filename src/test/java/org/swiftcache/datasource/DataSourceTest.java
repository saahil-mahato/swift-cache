package org.swiftcache.datasource;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.util.TestDatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link DataSource} class.
 * <p>
 * This test class verifies the behavior of the {@link DataSource} class by testing its
 * {@link DataSource#fetch(Object, String)}, {@link DataSource#store(Object, Object, String)}, and
 * {@link DataSource#delete(Object, String)} methods. The tests ensure that data can be correctly
 * stored, fetched, and deleted from the database.
 * </p>
 */
public class DataSourceTest {

    private DataSource<String, Integer> dataSource;
    private Connection connection;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    /**
     * Sets up the test environment by establishing a database connection and initializing the {@link DataSource}.
     * This method is run before each test case to ensure a fresh environment.
     *
     * @throws SQLException if there is an error obtaining the database connection
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
    }

    /**
     * Inserts test data into the database for testing purposes.
     *
     * @param key the key to be inserted
     * @param value the value to be associated with the key
     * @throws SQLException if there is an error executing the SQL statement
     */
    private void insertTestData(String key, int value) throws SQLException {
        connection.createStatement().execute(
                String.format("INSERT INTO test_table (testKey, testValue) VALUES ('%s', %d)", key, value)
        );
    }

    /**
     * Tests the {@link DataSource#fetch(Object, String)} method to ensure it correctly retrieves
     * data from the database.
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testFetch() throws SQLException {
        insertTestData("key1", 42);

        Integer result = dataSource.fetch("key1", SELECT_SQL);
        assertEquals(Integer.valueOf(42), result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link DataSource#store(Object, Object, String)} method to ensure it correctly
     * stores data in the database.
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testStore() throws SQLException {
        dataSource.store("key2", 43, INSERT_SQL);

        Integer result = dataSource.fetch("key2", SELECT_SQL);
        assertEquals(Integer.valueOf(43), result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link DataSource#delete(Object, String)} method to ensure it correctly removes
     * data from the database.
     *
     * @throws SQLException if there is an error accessing the database
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
