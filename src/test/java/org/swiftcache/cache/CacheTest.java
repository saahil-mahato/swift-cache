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
 * Unit tests for the {@link Cache} class.
 * <p>
 * This test class verifies the behavior of the {@link Cache} class with various combinations of
 * eviction strategies, reading policies, and writing policies. The tests ensure that the cache
 * performs correctly in terms of putting, getting, removing, sizing, and clearing cache entries.
 * </p>
 */
public class CacheTest {

    private DataSource<String, Integer> dataSource;
    private Connection connection;
    private static final Logger logger = Logger.getLogger(DataSource.class.getName());

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    /**
     * Sets up the test environment by establishing a database connection and initializing the {@link DataSource}.
     * This method is run before each test case to ensure a fresh environment.
     *
     * @throws SQLException if there is an error obtaining the database connection
     */
    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String,Integer>(connection);
    }

    /**
     * Pauses the execution for a short time to allow asynchronous operations to complete.
     * <p>
     * This method is used to ensure that database operations are fully completed before assertions are made.
     * </p>
     */
    private void pauseExecution() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "An error occurred while pausing execution", e);
        }
    }

    /**
     * Tests the {@link Cache#put(Object, Object, String)} method by inserting a value into the cache
     * and verifying that it is correctly stored in the data source.
     *
     * @param cache the cache instance to test
     * @param key the key to be used for storing the value
     * @param value the value to be stored
     * @throws SQLException if there is an error accessing the database
     */
    private void testPut(Cache<String, Integer> cache, String key, Integer value) throws SQLException {
        cache.put(key, value, INSERT_SQL);
        pauseExecution();
        Integer result = dataSource.fetch(key, SELECT_SQL);
        assertEquals(value, result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link Cache#get(Object, String)} method by putting a value into the cache
     * and verifying that it can be retrieved correctly.
     *
     * @param cache the cache instance to test
     * @param key the key used to store and retrieve the value
     * @param value the value to be stored and retrieved
     * @throws SQLException if there is an error accessing the database
     */
    private void testGet(Cache<String, Integer> cache, String key, Integer value) throws SQLException {
        cache.put(key, value, INSERT_SQL);
        pauseExecution();
        Integer result = cache.get(key, SELECT_SQL);
        assertEquals(value, result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link Cache#remove(Object, String)} method by removing a key from the cache
     * and verifying that it is also removed from the data source.
     *
     * @param cache the cache instance to test
     * @param key the key to be removed
     * @throws SQLException if there is an error accessing the database
     */
    private void testRemove(Cache<String, Integer> cache, String key) throws SQLException {
        cache.put(key, 1, INSERT_SQL);
        pauseExecution();
        cache.remove(key, DELETE_SQL);
        Integer result = dataSource.fetch(key, SELECT_SQL);
        assertNull(result);
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link Cache#size()} method to ensure it returns the correct number of entries in the cache.
     *
     * @param cache the cache instance to test
     * @param key1 the first key to be used
     * @param value1 the value for the first key
     * @param key2 the second key to be used
     * @param value2 the value for the second key
     * @throws SQLException if there is an error accessing the database
     */
    private void testSize(Cache<String, Integer> cache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        cache.put(key1, value1, INSERT_SQL);
        cache.put(key2, value2, INSERT_SQL);
        pauseExecution();
        assertEquals(2, cache.size());
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Tests the {@link Cache#clear()} method to ensure it removes all entries from the cache.
     *
     * @param cache the cache instance to test
     * @param key1 the first key to be used
     * @param value1 the value for the first key
     * @param key2 the second key to be used
     * @param value2 the value for the second key
     * @throws SQLException if there is an error accessing the database
     */
    private void testClear(Cache<String, Integer> cache, String key1, Integer value1, String key2, Integer value2) throws SQLException {
        cache.put(key1, value1, INSERT_SQL);
        cache.put(key2, value2, INSERT_SQL);
        pauseExecution();
        cache.clear();
        assertEquals(0, cache.size());
        TestDatabaseUtil.clearTable(connection);
    }

    /**
     * Runs tests for all combinations of writing policies, reading policies, and eviction strategies.
     * <p>
     * This method ensures that the {@link Cache} class works correctly with different configurations of
     * policies and strategies by running a series of tests for each combination.
     * </p>
     *
     * @throws SQLException if there is an error accessing the database
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
     * <p>
     * This method creates a {@link Cache} instance with the specified policies and strategies and
     * runs a series of tests to verify the cache's behavior.
     * </p>
     *
     * @param writingPolicy the writing policy to be used
     * @param readingPolicy the reading policy to be used
     * @param evictionStrategy the eviction strategy to be used
     * @throws SQLException if there is an error accessing the database
     */
    private void testCombination(IWritingPolicy<String, Integer> writingPolicy, IReadingPolicy<String, Integer> readingPolicy, IEvictionStrategy<String, Integer> evictionStrategy) throws SQLException {
        Cache<String, Integer> cache = new Cache<String,Integer>(3, dataSource, evictionStrategy, writingPolicy, readingPolicy);
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
