package org.swiftcache.readingpolicy;

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
 * Unit tests for the {@link RefreshAheadPolicy} class.
 *
 * Tests the Refresh Ahead policy for cache reading. This policy fetches data
 * from the data source and refreshes the cache after a specified interval.
 *
 * Uses JUnit for the test framework.
 *
 * @see RefreshAheadPolicy
 */
public class RefreshAheadPolicyTest {

    private Connection connection;
    private DataSource<String, Integer> dataSource;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String TEST_KEY = "key1";
    private static final int REFRESH_INTERVAL = 100; // Interval for cache refresh in milliseconds
    private static final int WAIT_TIME = 200; // Time to wait after refresh in milliseconds

    /**
     * Sets up the test environment by establishing a database connection
     * and creating a new {@link DataSource} instance.
     *
     * @throws SQLException if a database access error occurs
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
    }

    /**
     * Tests the {@link RefreshAheadPolicy#readWithDataSource} method.
     * Verifies that the policy correctly refreshes the cache after the specified
     * interval if the data is updated in the data source.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testReadWithDataSource() throws InterruptedException, SQLException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<>(REFRESH_INTERVAL);
        Map<String, Integer> cacheMap = new HashMap<>();

        int initialValue = 42;
        int updatedValue = 43;

        // Store the updated value in the data source and the initial value in the cache
        dataSource.store(TEST_KEY, updatedValue, INSERT_SQL);
        cacheMap.put(TEST_KEY, initialValue);

        // Fetch the value from the data source and using the policy
        Integer dataResult = dataSource.fetch(TEST_KEY, SELECT_SQL);
        Integer policyResult = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);

        // Assert that the initial value is in the cache
        assertEquals(Integer.valueOf(initialValue), policyResult);

        // Wait for the refresh interval to pass
        Thread.sleep(WAIT_TIME);

        // Fetch the value again using the policy after the refresh interval
        Integer policyResultRefreshed = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);
        assertEquals(dataResult, policyResultRefreshed);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link RefreshAheadPolicy#read} method.
     * Verifies that the Refresh Ahead policy throws an
     * {@link UnsupportedOperationException} when the method is called
     * without a data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRead() throws SQLException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<>(1);
        Map<String, Integer> cacheMap = new HashMap<>();
        policy.read(cacheMap, TEST_KEY);
        TestDatabaseUtil.clearTable(connection);
    }
}
