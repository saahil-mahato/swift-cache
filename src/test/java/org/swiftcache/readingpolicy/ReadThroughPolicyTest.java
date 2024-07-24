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
import static org.junit.Assert.assertNull;

/**
 * Unit tests for the {@link ReadThroughPolicy} class.
 *
 * Tests the Read Through policy for cache reading. This policy fetches data
 * from the data source if it is not present in the cache and updates the cache
 * with the fetched data.
 *
 * Uses JUnit for test framework.
 *
 * @see ReadThroughPolicy
 */
public class ReadThroughPolicyTest {

    private Connection connection;
    private DataSource<String, Integer> dataSource;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";

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
     * Tests the {@link ReadThroughPolicy#readWithDataSource} method.
     * Verifies that the policy correctly fetches data from the data source
     * and updates the cache when the cache does not contain the requested data.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testReadWithDataSource() throws SQLException {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<>();
        Map<String, Integer> cacheMap = new HashMap<>();
        String testKey = "key1";
        Integer testValue = 42;

        // Store value in the data source
        dataSource.store(testKey, testValue, INSERT_SQL);

        // Fetch the value using the data source directly
        Integer dataResult = dataSource.fetch(testKey, SELECT_SQL);

        // Fetch the value using the read-through policy
        Integer policyResult = policy.readWithDataSource(cacheMap, testKey, dataSource, SELECT_SQL);

        // Verify that the results are as expected
        assertEquals(dataResult, policyResult);
        assertEquals(dataResult, cacheMap.get(testKey));
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link ReadThroughPolicy#read} method.
     * Verifies that the Read Through policy throws an
     * {@link UnsupportedOperationException} when the method is called
     * without a data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRead() throws SQLException {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<>();
        Map<String, Integer> cacheMap = new HashMap<>();
        policy.read(cacheMap, "key1");
        TestDatabaseUtil.clearTable(connection);
    }
}
