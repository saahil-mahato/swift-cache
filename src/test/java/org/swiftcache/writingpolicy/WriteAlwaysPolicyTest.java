package org.swiftcache.writingpolicy;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.util.TestDatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link WriteAlwaysPolicy} class.
 *
 * Tests the behavior of the {@link WriteAlwaysPolicy} when writing data to both
 * the cache and the data source. The tests use an in-memory H2 database for storing
 * the data and verifying the operations.
 */
public class WriteAlwaysPolicyTest {

    // Constants for testing
    private static final String TEST_KEY = "key1";
    private static final Integer TEST_VALUE = 42;
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";

    // Database connection and data source
    private Connection connection;
    private DataSource<String, Integer> dataSource;

    // Write policy to be tested
    private WriteAlwaysPolicy<String, Integer> policy;

    // In-memory cache map
    private Map<String, Integer> cacheMap;

    /**
     * Sets up the test environment.
     *
     * Initializes the database connection, data source, write policy, and cache map.
     *
     * @throws SQLException if a database access error occurs
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new WriteAlwaysPolicy<>();
        cacheMap = new HashMap<>();
    }

    /**
     * Tests the {@link WriteAlwaysPolicy#write} method.
     *
     * Verifies that the policy writes data to both the cache and the data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testWrite() throws SQLException {
        policy.write(cacheMap, TEST_KEY, TEST_VALUE, dataSource, INSERT_SQL);

        // Verify that the value is written to the cache
        assertEquals(TEST_VALUE, cacheMap.get(TEST_KEY));

        // Verify that the value is written to the data source
        Integer result = dataSource.fetch(TEST_KEY, "SELECT testValue FROM test_table WHERE testKey = ?");
        assertEquals(TEST_VALUE, result);

        // Clean up the test data
        TestDatabaseUtil.clearTable(connection);
    }
}
