package org.swiftcache.cache;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.FIFOEvictionStrategy;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.evictionstrategy.LRUEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.readingpolicy.ReadThroughPolicy;
import org.swiftcache.readingpolicy.RefreshAheadPolicy;
import org.swiftcache.readingpolicy.SimpleReadPolicy;
import org.swiftcache.util.TestDatabaseUtil;
import org.swiftcache.writingpolicy.IWritingPolicy;
import org.swiftcache.writingpolicy.WriteAlwaysPolicy;
import org.swiftcache.writingpolicy.WriteBehindPolicy;
import org.swiftcache.writingpolicy.WriteIfAbsentPolicy;

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

    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String, Integer>(connection);
    }

    private void clearTable() throws SQLException {
        connection.createStatement().execute("DELETE FROM test_table");
    }

    private void pauseExecution() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "An error occurred while pausing execution", e);
        }
    }

    private void testPut(Cache<String, Integer> cache, String key, Integer value) throws SQLException {
        clearTable();
        cache.put(key, value, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        pauseExecution();
        Integer result = dataSource.fetch(key, "SELECT testValue FROM test_table WHERE testKey = ?");
        assertEquals(value, result);
    }

    private void testGet(Cache<String, Integer> cache, String key, Integer value) throws SQLException {
        clearTable();
        cache.put(key, value, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        pauseExecution();
        Integer result = cache.get(key, "SELECT testValue FROM test_table WHERE testKey = ?");
        assertEquals(value, result);
    }

    private void testRemove(Cache<String, Integer> cache, String key) throws SQLException {
        clearTable();
        cache.put(key, 1, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        pauseExecution();
        cache.remove(key, "DELETE FROM test_table WHERE testKey = ?");
        Integer result = dataSource.fetch(key, "SELECT testValue FROM test_table WHERE testKey = ?");
        assertNull(result);
    }

    private void testSize(Cache<String, Integer> cache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        clearTable();
        cache.put(key1, value1, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        cache.put(key2, value2, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        pauseExecution();
        assertEquals(2, cache.size());
    }

    private void testClear(Cache<String, Integer> cache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        clearTable();
        cache.put(key1, value1, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        cache.put(key2, value2, "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)");
        pauseExecution();
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    public void testAllCombinations() throws SQLException {
        testCombination(new WriteAlwaysPolicy<String, Integer>(), new ReadThroughPolicy<String, Integer>(), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteAlwaysPolicy<String, Integer>(), new ReadThroughPolicy<String, Integer>(), new LRUEvictionStrategy<String, Integer>());
        testCombination(new WriteAlwaysPolicy<String, Integer>(), new RefreshAheadPolicy<String, Integer>(1000), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteAlwaysPolicy<String, Integer>(), new RefreshAheadPolicy<String, Integer>(1000), new LRUEvictionStrategy<String, Integer>());
        testCombination(new WriteAlwaysPolicy<String, Integer>(), new SimpleReadPolicy<String, Integer>(), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteAlwaysPolicy<String, Integer>(), new SimpleReadPolicy<String, Integer>(), new LRUEvictionStrategy<String, Integer>());

        testCombination(new WriteBehindPolicy<String, Integer>(), new ReadThroughPolicy<String, Integer>(), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteBehindPolicy<String, Integer>(), new ReadThroughPolicy<String, Integer>(), new LRUEvictionStrategy<String, Integer>());
        testCombination(new WriteBehindPolicy<String, Integer>(), new RefreshAheadPolicy<String, Integer>(1000), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteBehindPolicy<String, Integer>(), new RefreshAheadPolicy<String, Integer>(1000), new LRUEvictionStrategy<String, Integer>());
        testCombination(new WriteBehindPolicy<String, Integer>(), new SimpleReadPolicy<String, Integer>(), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteBehindPolicy<String, Integer>(), new SimpleReadPolicy<String, Integer>(), new LRUEvictionStrategy<String, Integer>());

        testCombination(new WriteIfAbsentPolicy<String, Integer>(), new ReadThroughPolicy<String, Integer>(), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteIfAbsentPolicy<String, Integer>(), new ReadThroughPolicy<String, Integer>(), new LRUEvictionStrategy<String, Integer>());
        testCombination(new WriteIfAbsentPolicy<String, Integer>(), new RefreshAheadPolicy<String, Integer>(1000), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteIfAbsentPolicy<String, Integer>(), new RefreshAheadPolicy<String, Integer>(1000), new LRUEvictionStrategy<String, Integer>());
        testCombination(new WriteIfAbsentPolicy<String, Integer>(), new SimpleReadPolicy<String, Integer>(), new FIFOEvictionStrategy<String, Integer>());
        testCombination(new WriteIfAbsentPolicy<String, Integer>(), new SimpleReadPolicy<String, Integer>(), new LRUEvictionStrategy<String, Integer>());
    }

    private void testCombination(IWritingPolicy<String, Integer> writingPolicy, IReadingPolicy<String, Integer> readingPolicy, IEvictionStrategy<String, Integer> evictionStrategy) throws SQLException {
        Cache<String, Integer> cache = new Cache<String, Integer>(3, dataSource, evictionStrategy, writingPolicy, readingPolicy);
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
