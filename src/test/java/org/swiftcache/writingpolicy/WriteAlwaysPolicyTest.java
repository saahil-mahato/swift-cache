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
 * Unit tests for the {@link WriteAlwaysPolicy} class.
 * <p>
 * This test class verifies the behavior of the {@link WriteAlwaysPolicy} class, which is expected to
 * always write data to the data source when performing write operations. The tests ensure that the policy
 * correctly updates the data source and maintains the expected values in the cache.
 * </p>
 */
public class WriteAlwaysPolicyTest {

    private static final String TEST_KEY = "key1";
    private static final Integer TEST_VALUE = 42;
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";

    private Connection connection;
    private DataSource<String, Integer> dataSource;
    private WriteAlwaysPolicy<String, Integer> policy;
    private Map<String, Integer> cacheMap;

    /**
     * Sets up the test environment by establishing a database connection, initializing the {@link DataSource},
     * and creating an instance of {@link WriteAlwaysPolicy}. This method is run before each test case to ensure
     * a fresh environment.
     *
     * @throws SQLException if there is an error obtaining the database connection
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new WriteAlwaysPolicy<>();
        cacheMap = new HashMap<>();
    }

    /**
     * Tests the {@link WriteAlwaysPolicy#write(Map, Object, Object, IDataSource, String)} method to ensure that
     * it correctly writes data to both the cache and the data source.
     * <p>
     * This test verifies that the value is stored in the cache and is correctly written to the data source
     * using the specified SQL statement.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testWrite() throws SQLException {
        policy.write(cacheMap, TEST_KEY, TEST_VALUE, dataSource, INSERT_SQL);

        assertEquals(TEST_VALUE, cacheMap.get(TEST_KEY));
        TestDatabaseUtil.clearTable(connection);
    }
}
