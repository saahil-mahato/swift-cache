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

/**
 * Unit tests for the Cache class, covering various combinations of eviction strategies,
 * reading policies, and writing policies.
 */
public class SwiftCacheTest {

    private DataSource<String, Integer> dataSource;
    private Connection connection;
    private static final Logger logger = Logger.getLogger(SwiftCacheTest.class.getName());

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    /**
     * Initializes the database connection and DataSource before each test.
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
    }

    /**
     * Pauses execution for a short period to allow for asynchronous operations to complete.
     */
    private void pauseExecution() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "An error occurred while pausing execution", e);
        }
    }

    /**
     * Tests the `put` method of the Cache class.
     *
     * @param swiftCache the cache instance to test
     * @param key the key to insert
     * @param value the value to insert
     * @throws SQLException if a database access error occurs
     */
    private void testPut(SwiftCache<String, Integer> swiftCache, String key, Integer value) throws SQLException {
        swiftCache.put(key, value, INSERT_SQL);
        pauseExecution();
        Integer result = dataSource.fetch(key, SELECT_SQL);
        assertEquals(value, result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the `get` method of the Cache class.
     *
     * @param swiftCache the cache instance to test
     * @param key the key to retrieve
     * @param value the expected value
     * @throws SQLException if a database access error occurs
     */
    private void testGet(SwiftCache<String, Integer> swiftCache, String key, Integer value) throws SQLException {
        swiftCache.put(key, value, INSERT_SQL);
        pauseExecution();
        Integer result = swiftCache.get(key, SELECT_SQL);
        assertEquals(value, result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the `remove` method of the Cache class.
     *
     * @param swiftCache the cache instance to test
     * @param key the key to remove
     * @throws SQLException if a database access error occurs
     */
    private void testRemove(SwiftCache<String, Integer> swiftCache, String key) throws SQLException {
        swiftCache.put(key, 1, INSERT_SQL);
        pauseExecution();
        swiftCache.remove(key, DELETE_SQL);
        Integer result = dataSource.fetch(key, SELECT_SQL);
        assertNull(result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the `size` method of the Cache class.
     *
     * @param swiftCache the cache instance to test
     * @param key1 the first key
     * @param value1 the value for the first key
     * @param key2 the second key
     * @param value2 the value for the second key
     * @throws SQLException if a database access error occurs
     */
    private void testSize(SwiftCache<String, Integer> swiftCache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        swiftCache.put(key1, value1, INSERT_SQL);
        swiftCache.put(key2, value2, INSERT_SQL);
        pauseExecution();
        assertEquals(2, swiftCache.size());
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the `clear` method of the Cache class.
     *
     * @param swiftCache the cache instance to test
     * @param key1 the first key
     * @param value1 the value for the first key
     * @param key2 the second key
     * @param value2 the value for the second key
     * @throws SQLException if a database access error occurs
     */
    private void testClear(SwiftCache<String, Integer> swiftCache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        swiftCache.put(key1, value1, INSERT_SQL);
        swiftCache.put(key2, value2, INSERT_SQL);
        pauseExecution();
        swiftCache.clear();
        assertEquals(0, swiftCache.size());
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests all combinations of writing policies, reading policies, and eviction strategies.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    @SuppressWarnings("unchecked")
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

    /**
     * Tests a combination of writing policy, reading policy, and eviction strategy.
     *
     * @param writingPolicy the writing policy to test
     * @param readingPolicy the reading policy to test
     * @param evictionStrategy the eviction strategy to test
     * @throws SQLException if a database access error occurs
     */
    private void testCombination(IWritingPolicy<String, Integer> writingPolicy, IReadingPolicy<String, Integer> readingPolicy, IEvictionStrategy<String, Integer> evictionStrategy) throws SQLException {
        SwiftCache<String, Integer> swiftCache = new SwiftCache<>(3, dataSource, evictionStrategy, writingPolicy, readingPolicy);
        String key1 = "key1";
        Integer value1 = 1;
        String key2 = "key2";
        Integer value2 = 2;

        testPut(swiftCache, key1, value1);
        testGet(swiftCache, key1, value1);
        testRemove(swiftCache, key1);
        testSize(swiftCache, key1, value1, key2, value2);
        testClear(swiftCache, key1, value1, key2, value2);
    }
}
