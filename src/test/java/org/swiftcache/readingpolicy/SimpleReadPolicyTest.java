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
 * Unit tests for the {@link SimpleReadPolicy} class.
 *
 * Tests the Simple Read policy for cache reading. This policy reads data
 * from the cache without considering the data source, effectively ignoring
 * any external data source provided.
 *
 * Uses JUnit for the test framework.
 *
 * @see SimpleReadPolicy
 */
public class SimpleReadPolicyTest {

    private Connection connection;
    private DataSource<String, Integer> dataSource;
    private SimpleReadPolicy<String, Integer> policy;
    private Map<String, Integer> cacheMap;

    private static final String TEST_KEY = "key1";
    private static final Integer TEST_VALUE = 42;
    private static final String SELECT_SQL = "SELECT value FROM table WHERE key = ?";

    /**
     * Sets up the test environment by establishing a database connection,
     * creating a new {@link DataSource} instance, and initializing the
     * {@link SimpleReadPolicy} and cache map.
     *
     * @throws SQLException if a database access error occurs
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new SimpleReadPolicy<>();
        cacheMap = new HashMap<>();
        cacheMap.put(TEST_KEY, TEST_VALUE);
    }

    /**
     * Tests the {@link SimpleReadPolicy#read} method.
     * Verifies that the policy correctly reads the value from the cache
     * without considering the data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testRead() throws SQLException {
        Integer result = policy.read(cacheMap, TEST_KEY);
        assertEquals(TEST_VALUE, result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link SimpleReadPolicy#readWithDataSource} method.
     * Verifies that the policy reads the value from the cache and ignores
     * the data source provided.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testReadWithDataSource() throws SQLException {
        Integer result = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);
        assertEquals(TEST_VALUE, result);
        TestDatabaseUtil.clearTable(connection);
    }
}
