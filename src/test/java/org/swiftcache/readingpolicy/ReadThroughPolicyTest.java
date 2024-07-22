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

    private Connection connection;
    private DataSource<String, Integer> dataSource;

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";

    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
    }

    @Test
    public void testReadWithDataSource() throws SQLException {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<>();
        Map<String, Integer> cacheMap = new HashMap<>();
        String testKey = "key1";
        Integer testValue = 42;

        dataSource.store(testKey, testValue, INSERT_SQL);

        Integer dataResult = dataSource.fetch(testKey, SELECT_SQL);
        Integer policyResult = policy.readWithDataSource(cacheMap, testKey, dataSource, SELECT_SQL);

        assertEquals(dataResult, policyResult);
        assertEquals(dataResult, cacheMap.get(testKey));
        TestDatabaseUtil.clearTable(connection);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRead() throws SQLException {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<>();
        Map<String, Integer> cacheMap = new HashMap<>();
        policy.read(cacheMap, "key1");
        TestDatabaseUtil.clearTable(connection);
    }
}