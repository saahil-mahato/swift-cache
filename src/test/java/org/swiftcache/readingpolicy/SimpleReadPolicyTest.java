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
 * Unit tests for the {@link SimpleReadPolicy} class.
 * <p>
 * This test class verifies the behavior of the {@link SimpleReadPolicy} class, which implements
 * a basic caching policy for reading values directly from the cache. The tests ensure that the policy
 * correctly retrieves values from the cache and handles data source interactions appropriately.
 * </p>
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
     * Sets up the test environment by establishing a database connection, initializing the {@link DataSource},
     * and setting up the {@link SimpleReadPolicy} and cache map.
     * This method is run before each test case to ensure a fresh environment.
     *
     * @throws SQLException if there is an error obtaining the database connection
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String,Integer>(connection);
        policy = new SimpleReadPolicy<String,Integer>();
        cacheMap = new HashMap<String,Integer>();
        cacheMap.put(TEST_KEY, TEST_VALUE);
    }

    /**
     * Tests the {@link SimpleReadPolicy#read(Map, Object)} method to ensure that it correctly retrieves
     * the value from the cache.
     * <p>
     * This test verifies that the policy can fetch the value associated with the key from the cache map
     * and that it matches the expected value.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testRead() throws SQLException {
        Integer result = policy.read(cacheMap, TEST_KEY);
        assertEquals(TEST_VALUE, result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link SimpleReadPolicy#readWithDataSource(Map, Object, IDataSource, String)} method
     * to ensure that it correctly retrieves the value from the cache when a data source and SQL query
     * are provided.
     * <p>
     * This test verifies that the policy can fetch the value associated with the key from the cache map
     * and also ensures that the data source and SQL query parameters do not affect the result.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testReadWithDataSource() throws SQLException {
        Integer result = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);
        assertEquals(TEST_VALUE, result);
        TestDatabaseUtil.clearTable(connection);
    }
}
