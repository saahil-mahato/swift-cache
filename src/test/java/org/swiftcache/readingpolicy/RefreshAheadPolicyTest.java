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

    private DataSource<String, Integer> dataSource;

    @Before
    public void setUp() throws SQLException {
        Connection connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String, Integer>(connection);
    }

    @Test
    public void testReadWithDataSource() throws InterruptedException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<String, Integer>(100);
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();

        dataSource.store("key1", 43, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        cacheMap.put("key1", 42);

        Integer dataResult = dataSource.fetch("key1", "SELECT testValue FROM test_table WHERE testKey = ?");
        Integer policyResult = policy.readWithDataSource(cacheMap, "key1", dataSource, "SELECT testValue FROM test_table WHERE testKey = ?");

        assertEquals(Integer.valueOf(43), dataResult);
        assertEquals(Integer.valueOf(42), policyResult);

        // Wait for the refresh to occur
        Thread.sleep(200);

        Integer policyResultRefreshed = policy.readWithDataSource(cacheMap, "key1", dataSource, "SELECT testValue FROM test_table WHERE testKey = ?");
        assertEquals(dataResult, policyResultRefreshed);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRead() {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<String, Integer>(1);
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        policy.read(cacheMap, "key1");
    }
}