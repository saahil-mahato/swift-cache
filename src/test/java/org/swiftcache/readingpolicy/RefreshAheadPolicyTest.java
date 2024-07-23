package org.swiftcache.readingpolicy;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.datasource.IDataSource;
import org.swiftcache.util.TestDatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link RefreshAheadPolicy} class.
 * <p>
 * This test class verifies the behavior of the {@link RefreshAheadPolicy} class, which
 * implements a refresh-ahead caching policy. The tests ensure that the policy correctly
 * updates the cache based on the refresh interval and handles unsupported operations appropriately.
 * </p>
 */
public class RefreshAheadPolicyTest {

    private Connection connection;
    private DataSource<String, Integer> dataSource;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String TEST_KEY = "key1";
    private static final int REFRESH_INTERVAL = 100;
    private static final int WAIT_TIME = 200;

    /**
     * Sets up the test environment by establishing a database connection and initializing the {@link DataSource}.
     * This method is run before each test case to ensure a fresh environment.
     *
     * @throws SQLException if there is an error obtaining the database connection
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String,Integer>(connection);
    }

    /**
     * Tests the {@link RefreshAheadPolicy#readWithDataSource(Map, Object, IDataSource, String)} method
     * to ensure that it correctly reads data from the data source and updates the cache based on the
     * refresh interval.
     * <p>
     * This test stores an initial value in the cache and a new value in the data source. It then verifies
     * that the initial value is retrieved from the cache. After waiting for the refresh interval to pass,
     * it checks that the cache has been updated with the new value from the data source.
     * </p>
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testReadWithDataSource() throws InterruptedException, SQLException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<String,Integer>(REFRESH_INTERVAL);
        Map<String, Integer> cacheMap = new HashMap<String,Integer>();

        int initialValue = 42;
        int updatedValue = 43;

        // Store the updated value in the data source and the initial value in the cache
        dataSource.store(TEST_KEY, updatedValue, INSERT_SQL);
        cacheMap.put(TEST_KEY, initialValue);

        // Fetch the value from the data source and using the policy
        Integer dataResult = dataSource.fetch(TEST_KEY, SELECT_SQL);
        Integer policyResult = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);

        // Assert that the values are as expected
        assertEquals(Integer.valueOf(updatedValue), dataResult);
        assertEquals(Integer.valueOf(initialValue), policyResult);

        // Wait for the refresh interval to pass
        Thread.sleep(WAIT_TIME);

        // Fetch the value again using the policy after the refresh interval
        Integer policyResultRefreshed = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);
        assertEquals(dataResult, policyResultRefreshed);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link RefreshAheadPolicy#read(Map, Object)} method to ensure that it throws an
     * {@link UnsupportedOperationException} since the refresh-ahead policy requires a data source.
     * <p>
     * This test verifies that attempting to use the basic read method results in the expected exception,
     * as the refresh-ahead policy is designed to work with a data source and does not support direct reading.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRead() throws SQLException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<String,Integer>(1);
        Map<String, Integer> cacheMap = new HashMap<String,Integer>();
        policy.read(cacheMap, TEST_KEY);
        TestDatabaseUtil.clearTable(connection);
    }
}
