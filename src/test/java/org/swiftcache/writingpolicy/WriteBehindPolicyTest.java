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

    private DataSource<String, Integer> dataSource;

    @Before
    public void setUp() throws SQLException {
        Connection connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String, Integer>(connection);
    }

    @Test
    public void testWrite() throws InterruptedException {
        WriteBehindPolicy<String, Integer> policy = new WriteBehindPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();

        policy.write(cacheMap, "key1", 42, dataSource, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");

        Integer dataResult = dataSource.fetch("key1", "SELECT testValue FROM test_table WHERE testKey = ?");

        assertEquals(Integer.valueOf(42), cacheMap.get("key1"));
        assertNull(dataResult);

        // Wait for the asynchronous write to occur
        Thread.sleep(200);

        Integer dataResultRefreshed = dataSource.fetch("key1", "SELECT testValue FROM test_table WHERE testKey = ?");

        assertEquals(cacheMap.get("key1"), dataResultRefreshed);
    }
}
