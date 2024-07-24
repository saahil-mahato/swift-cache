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
import static org.junit.Assert.assertNull;

/**
 * Unit tests for the {@link WriteBehindPolicy} class.
 *
 * Tests the behavior of the {@link WriteBehindPolicy} when writing data to the cache and asynchronously
 * to the data source. The tests use an in-memory H2 database to store and verify the data.
 */
public class WriteBehindPolicyTest {

    // Constants for testing
    private static final String TEST_KEY = "key1";
    private static final Integer TEST_VALUE = 42;
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final int WAIT_TIME = 200; // Time to wait for the asynchronous write to complete

    // Database connection and data source
    private Connection connection;
    private DataSource<String, Integer> dataSource;

    // Write policy to be tested
    private WriteBehindPolicy<String, Integer> policy;

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
        policy = new WriteBehindPolicy<>();
        cacheMap = new HashMap<>();
    }

    /**
     * Tests the {@link WriteBehindPolicy#write} method.
     *
     * Verifies that the policy writes data to the cache immediately but defers writing to the data source
     * asynchronously. The test waits for a short period to ensure the asynchronous write has time to complete.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testWrite() throws InterruptedException, SQLException {
        policy.write(cacheMap, TEST_KEY, TEST_VALUE, dataSource, INSERT_SQL);

        // Verify that the value is in the cache but not in the data source immediately
        Integer dataResult = dataSource.fetch(TEST_KEY, SELECT_SQL);
        assertEquals(TEST_VALUE, cacheMap.get(TEST_KEY));
        assertNull(dataResult);

        // Wait for the asynchronous write to complete
        Thread.sleep(WAIT_TIME);

        // Verify that the value is eventually written to the data source
        Integer dataResultRefreshed = dataSource.fetch(TEST_KEY, SELECT_SQL);
        assertEquals(cacheMap.get(TEST_KEY), dataResultRefreshed);

        // Clean up the test data
        TestDatabaseUtil.clearTable(connection);
    }
}
