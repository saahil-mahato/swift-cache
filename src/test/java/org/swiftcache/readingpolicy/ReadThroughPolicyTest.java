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
 * Unit tests for the {@link ReadThroughPolicy} class.
 * <p>
 * This test class verifies the behavior of the {@link ReadThroughPolicy} class, which
 * implements the read-through caching policy. The tests ensure that the policy reads data
 * correctly from the data source and that it handles unsupported operations as expected.
 * </p>
 */
public class ReadThroughPolicyTest {

    private Connection connection;
    private DataSource<String, Integer> dataSource;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";

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
     * Tests the {@link ReadThroughPolicy#readWithDataSource(Map, Object, IDataSource, String)} method
     * to ensure that it correctly reads data from the data source and updates the cache.
     * <p>
     * This test stores a value in the data source, then uses the read-through policy to read the value
     * from the data source through the policy and verifies that the value is correctly placed in the cache.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testReadWithDataSource() throws SQLException {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<String,Integer>();
        Map<String, Integer> cacheMap = new HashMap<String,Integer>();
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
     * Tests the {@link ReadThroughPolicy#read(Map, Object)} method to ensure that it throws an
     * {@link UnsupportedOperationException} as the read-through policy requires a data source.
     * <p>
     * This test verifies that attempting to use the basic read method results in the expected exception,
     * since the read-through policy is designed to work with a data source.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRead() throws SQLException {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<String,Integer>();
        Map<String, Integer> cacheMap = new HashMap<String,Integer>();
        policy.read(cacheMap, "key1");
        TestDatabaseUtil.clearTable(connection);
    }
}
