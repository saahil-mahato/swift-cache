package org.swiftcache.cache;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.util.TestDatabaseUtil;
import org.swiftcache.writingpolicy.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class CacheTest {

    private DataSource<String, Integer> dataSource;
    private Connection connection;
    private static final Logger logger = Logger.getLogger(DataSource.class.getName());

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
    }

    private void pauseExecution() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "An error occurred while pausing execution", e);
        }
    }

    private void testPut(Cache<String, Integer> cache, String key, Integer value) throws SQLException {
        cache.put(key, value, INSERT_SQL);
        pauseExecution();
        Integer result = dataSource.fetch(key, SELECT_SQL);
        assertEquals(value, result);
        TestDatabaseUtil.clearTable(connection);
    }

    private void testGet(Cache<String, Integer> cache, String key, Integer value) throws SQLException {
        cache.put(key, value, INSERT_SQL);
        pauseExecution();
        Integer result = cache.get(key, SELECT_SQL);
        assertEquals(value, result);
        TestDatabaseUtil.clearTable(connection);
    }

    private void testRemove(Cache<String, Integer> cache, String key) throws SQLException {
        cache.put(key, 1, INSERT_SQL);
        pauseExecution();
        cache.remove(key, DELETE_SQL);
        Integer result = dataSource.fetch(key, SELECT_SQL);
        assertNull(result);
        TestDatabaseUtil.clearTable(connection);
    }

    private void testSize(Cache<String, Integer> cache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        cache.put(key1, value1, INSERT_SQL);
        cache.put(key2, value2, INSERT_SQL);
        pauseExecution();
        assertEquals(2, cache.size());
        TestDatabaseUtil.clearTable(connection);
    }

    private void testClear(Cache<String, Integer> cache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        cache.put(key1, value1, INSERT_SQL);
        cache.put(key2, value2, INSERT_SQL);
        pauseExecution();
        cache.clear();
        assertEquals(0, cache.size());
        TestDatabaseUtil.clearTable(connection);
    }

    @Test
    public void testAllCombinations() throws SQLException {
        IWritingPolicy<String, Integer>[] writingPolicies = new IWritingPolicy[] {
                new WriteAlwaysPolicy<String, Integer>(),
                new WriteBehindPolicy<String, Integer>(),
                new WriteIfAbsentPolicy<String, Integer>()
        };

        IReadingPolicy<String, Integer>[] readingPolicies = new IReadingPolicy[] {
                new ReadThroughPolicy<String, Integer>(),
                new RefreshAheadPolicy<String, Integer>(1),
                new SimpleReadPolicy<String, Integer>()
        };

        IEvictionStrategy<String, Integer>[] evictionStrategies = new IEvictionStrategy[] {
                new FIFOEvictionStrategy<String, Integer>(),
                new LRUEvictionStrategy<String, Integer>()
        };

        for (IWritingPolicy<String, Integer> writingPolicy : writingPolicies) {
            for (IReadingPolicy<String, Integer> readingPolicy : readingPolicies) {
                for (IEvictionStrategy<String, Integer> evictionStrategy : evictionStrategies) {
                    testCombination(writingPolicy, readingPolicy, evictionStrategy);
                }
            }
        }
    }

    private void testCombination(IWritingPolicy<String, Integer> writingPolicy, IReadingPolicy<String, Integer> readingPolicy, IEvictionStrategy<String, Integer> evictionStrategy) throws SQLException {
        Cache<String, Integer> cache = new Cache<>(3, dataSource, evictionStrategy, writingPolicy, readingPolicy);
        String key1 = "key1";
        Integer value1 = 1;
        String key2 = "key2";
        Integer value2 = 2;

        testPut(cache, key1, value1);
        testGet(cache, key1, value1);
        testRemove(cache, key1);
        testSize(cache, key1, value1, key2, value2);
        testClear(cache, key1, value1, key2, value2);
    }
}
