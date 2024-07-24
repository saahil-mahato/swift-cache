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


public class WriteAlwaysPolicyTest {

    private static final String TEST_KEY = "key1";
    private static final Integer TEST_VALUE = 42;
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";

    private Connection connection;
    private DataSource<String, Integer> dataSource;
    private WriteAlwaysPolicy<String, Integer> policy;
    private Map<String, Integer> cacheMap;


    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new WriteAlwaysPolicy<>();
        cacheMap = new HashMap<>();
    }

    @Test
    public void testWrite() throws SQLException {
        policy.write(cacheMap, TEST_KEY, TEST_VALUE, dataSource, INSERT_SQL);

        assertEquals(TEST_VALUE, cacheMap.get(TEST_KEY));
        TestDatabaseUtil.clearTable(connection);
    }
}
