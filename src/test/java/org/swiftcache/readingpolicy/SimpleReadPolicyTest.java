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

public class SimpleReadPolicyTest {

    private Connection connection;
    private DataSource<String, Integer> dataSource;
    private SimpleReadPolicy<String, Integer> policy;
    private Map<String, Integer> cacheMap;

    private static final String TEST_KEY = "key1";
    private static final Integer TEST_VALUE = 42;
    private static final String SELECT_SQL = "SELECT value FROM table WHERE key = ?";

    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
        policy = new SimpleReadPolicy<>();
        cacheMap = new HashMap<>();
        cacheMap.put(TEST_KEY, TEST_VALUE);
    }

    @Test
    public void testRead() throws SQLException {
        Integer result = policy.read(cacheMap, TEST_KEY);
        assertEquals(TEST_VALUE, result);
        TestDatabaseUtil.clearTable(connection);
    }

    @Test
    public void testReadWithDataSource() throws SQLException {
        Integer result = policy.readWithDataSource(cacheMap, TEST_KEY, dataSource, SELECT_SQL);
        assertEquals(TEST_VALUE, result);
        TestDatabaseUtil.clearTable(connection);
    }
}