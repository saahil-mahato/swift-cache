package org.swiftcache.readingpolicy;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.util.TestDatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RefreshAheadPolicyTest {

    private Connection connection;
    private DataSource<String, Integer> dataSource;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String TEST_KEY = "key1";
    private static final int REFRESH_INTERVAL = 100;
    private static final int WAIT_TIME = 200;

    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
    }

    @Test
    public void testReadWithDataSource() throws InterruptedException, SQLException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<>(REFRESH_INTERVAL);
        Map<String, Integer> cacheMap = new HashMap<>();

        int initialValue = 42;
        int updatedValue = 43;

        dataSource.store(TEST_KEY, updatedValue, INSERT_SQL);
        cacheMap.put(TEST_KEY, initialValue);

        Integer dataResult = dataSource.fetch(TEST_KEY, SELECT_SQL);
        Integer policyResult = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);

        assertEquals(Integer.valueOf(updatedValue), dataResult);
        assertEquals(Integer.valueOf(initialValue), policyResult);

        // Wait for the refresh to occur
        Thread.sleep(WAIT_TIME);

        Integer policyResultRefreshed = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);
        assertEquals(dataResult, policyResultRefreshed);
        TestDatabaseUtil.clearTable(connection);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRead() throws SQLException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<>(1);
        Map<String, Integer> cacheMap = new HashMap<>();
        policy.read(cacheMap, TEST_KEY);
        TestDatabaseUtil.clearTable(connection);
    }
}