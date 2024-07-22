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

public class WriteIfAbsentPolicyTest {

    private DataSource<String, Integer> dataSource;

    @Before
    public void setUp() throws SQLException {
        Connection connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String, Integer>(connection);
    }

    @Test
    public void testWriteWhenAbsent() {
        WriteIfAbsentPolicy<String, Integer> policy = new WriteIfAbsentPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();

        policy.write(cacheMap, "key1", 42, dataSource, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");

        assertEquals(Integer.valueOf(42), cacheMap.get("key1"));
    }

    @Test
    public void testWriteWhenPresent() throws SQLException {
        WriteIfAbsentPolicy<String, Integer> policy = new WriteIfAbsentPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();

        cacheMap.put("key1", 42);
        policy.write(cacheMap, "key1", 43, dataSource, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");

        assertEquals(Integer.valueOf(42), cacheMap.get("key1"));
    }
}