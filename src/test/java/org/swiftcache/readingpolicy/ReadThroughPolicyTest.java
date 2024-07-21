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

public class ReadThroughPolicyTest {

    private DataSource<String, Integer> dataSource;

    @Before
    public void setUp() throws SQLException {
        Connection connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String, Integer>(connection);
    }

    @Test
    public void testReadWithDataSource() throws SQLException {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();

        cacheMap.put("key1", 42);

        Integer dataResult = dataSource.fetch("key1", "SELECT testValue FROM test_table WHERE testKey = ?");
        Integer policyResult = policy.readWithDataSource(cacheMap, "key1", dataSource, "SELECT testValue FROM test_table WHERE testKey = ?");

        assertEquals(dataResult, policyResult);
        assertEquals(dataResult, cacheMap.get("key1"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRead() {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        policy.read(cacheMap, "key1");
    }
}
