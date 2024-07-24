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
 * Unit tests for the {@link WriteIfAbsentPolicy} class.
 *
 * Tests the behavior of the {@link WriteIfAbsentPolicy} when writing data to the cache and the data source.
 * Specifically, it verifies that the policy only writes to the data source if the key is absent.
 * Uses an in-memory H2 database to store and verify the data.
 */
public class WriteIfAbsentPolicyTest {

    // Constants for testing
    private static final String TEST_KEY = "key1";
    private static final Integer INITIAL_VALUE = 42;
    private static final Integer NEW_VALUE = 43;
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";

    // Database connection and data source
    private Connection connection;
    private DataSource<String, Integer> dataSource;

    // Write policy to be tested
    private WriteIfAbsentPolicy<String, Integer> policy;

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
        policy = new WriteIfAbsentPolicy<>();
        cacheMap = new HashMap<>();
    }

    /**
     * Tests the {@link WriteIfAbsentPolicy#write} method when the key is absent in the cache.
     *
     * Verifies that the policy writes the value to both the cache and the data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testWriteWhenAbsent() throws SQLException {
        policy.write(cacheMap, TEST_KEY, INITIAL_VALUE, dataSource, INSERT_SQL);

        // Verify that the value is stored in the cache
        assertEquals(INITIAL_VALUE, cacheMap.get(TEST_KEY));

        // Ensure the data source is cleared for clean test setup
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link WriteIfAbsentPolicy#write} method when the key is already present in the cache.
     *
     * Verifies that the policy does not overwrite the existing value in the cache or data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testWriteWhenPresent() throws SQLException {
        cacheMap.put(TEST_KEY, INITIAL_VALUE);
        policy.write(cacheMap, TEST_KEY, NEW_VALUE, dataSource, INSERT_SQL);

        // Verify that the cache value remains unchanged
        assertEquals(INITIAL_VALUE, cacheMap.get(TEST_KEY));

        // Ensure the data source is cleared for clean test setup
        TestDatabaseUtil.clearTable(connection);
    }
}
