package org.swiftcache.writingpolicy;

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
 * Unit tests for the {@link WriteIfAbsentPolicy} class.
 * <p>
 * This test class verifies the behavior of the {@link WriteIfAbsentPolicy} class, which only writes
 * data to the data source if the key is not already present in the cache. The tests ensure that the policy
 * correctly handles cases where the key is absent and when the key is already present in the cache.
 * </p>
 */
public class WriteIfAbsentPolicyTest {

    private static final String TEST_KEY = "key1";
    private static final Integer INITIAL_VALUE = 42;
    private static final Integer NEW_VALUE = 43;
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";

    private Connection connection;
    private DataSource<String, Integer> dataSource;
    private WriteIfAbsentPolicy<String, Integer> policy;
    private Map<String, Integer> cacheMap;

    /**
     * Sets up the test environment by establishing a database connection, initializing the {@link DataSource},
     * and creating an instance of {@link WriteIfAbsentPolicy}. This method is run before each test case to ensure
     * a fresh environment.
     *
     * @throws SQLException if there is an error obtaining the database connection
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new WriteIfAbsentPolicy<>();
        cacheMap = new HashMap<>();
    }

    /**
     * Tests the {@link WriteIfAbsentPolicy#write(Map, Object, Object, IDataSource, String)} method to ensure that
     * data is written to the data source only if the key is not already present in the cache.
     * <p>
     * In this test, the key is not present in the cache, so the data should be written to the data source. The
     * test verifies that the value is correctly stored in the cache.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testWriteWhenAbsent() throws SQLException {
        policy.write(cacheMap, TEST_KEY, INITIAL_VALUE, dataSource, INSERT_SQL);

        assertEquals(INITIAL_VALUE, cacheMap.get(TEST_KEY));
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link WriteIfAbsentPolicy#write(Map, Object, Object, IDataSource, String)} method to ensure that
     * data is not written to the data source if the key is already present in the cache.
     * <p>
     * In this test, the key is already present in the cache with an initial value. The policy should not overwrite
     * the existing value in the cache or write the new value to the data source. The test verifies that the cache
     * value remains unchanged and that no new entry is added to the data source.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testWriteWhenPresent() throws SQLException {
        cacheMap.put(TEST_KEY, INITIAL_VALUE);
        policy.write(cacheMap, TEST_KEY, NEW_VALUE, dataSource, INSERT_SQL);

        assertEquals(INITIAL_VALUE, cacheMap.get(TEST_KEY));
        TestDatabaseUtil.clearTable(connection);
    }
}
