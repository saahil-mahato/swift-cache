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
 * Unit tests for the {@link WriteBehindPolicy} class.
 * <p>
 * This test class verifies the behavior of the {@link WriteBehindPolicy} class, which is expected to
 * store data in the cache immediately and perform asynchronous writes to the data source. The tests ensure
 * that the policy correctly handles the delayed writing of data to the data source and maintains the expected
 * values in the cache.
 * </p>
 */
public class WriteBehindPolicyTest {

    private static final String TEST_KEY = "key1";
    private static final Integer TEST_VALUE = 42;
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final int WAIT_TIME = 200;

    private Connection connection;
    private DataSource<String, Integer> dataSource;
    private WriteBehindPolicy<String, Integer> policy;
    private Map<String, Integer> cacheMap;

    /**
     * Sets up the test environment by establishing a database connection, initializing the {@link DataSource},
     * and creating an instance of {@link WriteBehindPolicy}. This method is run before each test case to ensure
     * a fresh environment.
     *
     * @throws SQLException if there is an error obtaining the database connection
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new WriteBehindPolicy<>();
        cacheMap = new HashMap<>();
    }

    /**
     * Tests the {@link WriteBehindPolicy#write(Map, Object, Object, IDataSource, String)} method to ensure that
     * it correctly writes data to the cache immediately while deferring the write operation to the data source.
     * <p>
     * This test verifies that the value is stored in the cache, but not immediately in the data source.
     * It then waits for the asynchronous write to occur and verifies that the data source eventually contains
     * the expected value.
     * </p>
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    public void testWrite() throws InterruptedException, SQLException {
        policy.write(cacheMap, TEST_KEY, TEST_VALUE, dataSource, INSERT_SQL);

        Integer dataResult = dataSource.fetch(TEST_KEY, SELECT_SQL);

        // Verify that the data source does not immediately contain the value
        assertEquals(TEST_VALUE, cacheMap.get(TEST_KEY));
        assertNull(dataResult);

        // Wait for the asynchronous write to occur
        Thread.sleep(WAIT_TIME);

        Integer dataResultRefreshed = dataSource.fetch(TEST_KEY, SELECT_SQL);

        // Verify that the data source eventually contains the value
        assertEquals(cacheMap.get(TEST_KEY), dataResultRefreshed);
        TestDatabaseUtil.clearTable(connection);
    }
}
