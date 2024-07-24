package org.swiftcache.writingpolicy;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.util.TestDatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


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


    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new WriteBehindPolicy<>();
        cacheMap = new HashMap<>();
    }

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
