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
import static org.mockito.Mockito.*;

public class SimpleReadPolicyTest {

    private DataSource<String, Integer> dataSource;

    @Before
    public void setUp() throws SQLException {
        Connection connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String, Integer>(connection);
    }

    @Test
    public void testRead() {
        SimpleReadPolicy<String, Integer> policy = new SimpleReadPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        cacheMap.put("key1", 42);

        Integer result = policy.read(cacheMap, "key1");

        assertEquals(Integer.valueOf(42), result);
    }

    @Test
    public void testReadWithDataSource() {
        SimpleReadPolicy<String, Integer> policy = new SimpleReadPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();

        cacheMap.put("key1", 42);
        Integer result = policy.readWithDataSource(cacheMap, "key1", dataSource, "SELECT value FROM table WHERE key = ?");

        assertEquals(Integer.valueOf(42), result);
    }
}
