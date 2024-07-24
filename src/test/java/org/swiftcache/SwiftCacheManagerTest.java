package org.swiftcache;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link SwiftCacheManager} class.
 *
 * This class tests various functionalities of the cache, including:
 * - Basic cache operations (put, get, remove, clear)
 * - Eviction strategy behavior
 * - Reading policy behavior
 * - Writing policy behavior
 *
 * Uses an in-memory H2 database for testing.
 */
public class SwiftCacheManagerTest {

    // SQL queries for testing
    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    // Cache instance and configuration
    private SwiftCache<Object, Object> swiftCache;
    private SwiftCacheConfig swiftCacheConfig;

    /**
     * Sets up the test environment.
     *
     * Initializes the cache configuration with test settings and creates a cache instance.
     * Also sets up the database table for testing.
     *
     * @throws SQLException if a database access error occurs
     */
    @Before
    public void setUp() throws SQLException {
        // Initialize CacheConfig with test settings
        swiftCacheConfig = new SwiftCacheConfig(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", // Use in-memory H2 database
                "sa",                                   // Default username for H2
                "",                                     // No password
                100,                                    // Max cache size
                "LRU",                                  // Eviction strategy
                "ReadThrough",                          // Reading policy
                "WriteAlways"                           // Writing policy
        );

        // Get cache instance using the config
        swiftCache = SwiftCacheManager.getCache(swiftCacheConfig);

        SwiftCacheManager.createTestTable();
    }

    /**
     * Tests the basic put and get operations of the cache.
     *
     * Verifies that a value put into the cache can be retrieved correctly.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testPutAndGet() throws SQLException {
        SwiftCacheManager.clearTestTable();

        swiftCache.put("key1", 1, INSERT_SQL);
        assertEquals("Value should be retrieved from cache", 1, swiftCache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the eviction strategy of the cache.
     *
     * Verifies that the least recently used entries are evicted when the cache exceeds its maximum size.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testEvictionStrategy() throws SQLException {
        SwiftCacheManager.clearTestTable();

        for (int i = 1; i <= swiftCacheConfig.getMaxSize(); ++i) {
            swiftCache.put("key" + i, i, INSERT_SQL);
        }

        // Access some keys to change their "recently used" status
        swiftCache.get("key1", SELECT_SQL);
        swiftCache.get("key2", SELECT_SQL);
        swiftCache.get("key3", SELECT_SQL);
        swiftCache.get("key4", SELECT_SQL);

        // Add new data to evict the least used data
        swiftCache.put("key" + (swiftCacheConfig.getMaxSize() + 1), swiftCacheConfig.getMaxSize() + 1, INSERT_SQL);

        // The least recently used key should have been evicted
        assertNull("Least recently used key should be evicted", swiftCache.get("key0", SELECT_SQL));

        // Recently accessed keys should still be in the cache
        assertEquals("Recently accessed key should be in cache", 1, swiftCache.get("key1", SELECT_SQL));
        assertEquals("Recently accessed key should be in cache", 2, swiftCache.get("key2", SELECT_SQL));
        assertEquals("Recently accessed key should be in cache", 3, swiftCache.get("key3", SELECT_SQL));
        assertEquals("Recently accessed key should be in cache", 4, swiftCache.get("key4", SELECT_SQL));
    }

    /**
     * Tests the reading policy of the cache.
     *
     * Verifies that the value can be retrieved from the data source if the cache is cleared.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testReadingPolicy() throws SQLException {
        SwiftCacheManager.clearTestTable();

        swiftCache.put("key1", 1, INSERT_SQL);
        assertEquals("Value should be retrieved from cache", 1, swiftCache.get("key1", SELECT_SQL));

        // Clear the cache to force a read from the data source
        swiftCache.clear();
        // This should trigger a read-through to the data source
        assertEquals("Value should be retrieved from data source", 1, swiftCache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the writing policy of the cache.
     *
     * Verifies that the value is correctly written to both the cache and the data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testWritingPolicy() throws SQLException {
        SwiftCacheManager.clearTestTable();

        swiftCache.put("key1", 1, INSERT_SQL);
        assertEquals("Value should be in both cache and data source", 1, swiftCache.get("key1", SELECT_SQL));

        // Clear the cache to force a read from the data source
        swiftCache.clear();
        assertEquals("Value should still be retrievable from data source", 1, swiftCache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the remove operation of the cache.
     *
     * Verifies that a removed item is no longer present in both the cache and the data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testRemove() throws SQLException {
        SwiftCacheManager.clearTestTable();

        swiftCache.put("key1", 1, INSERT_SQL);
        swiftCache.remove("key1", DELETE_SQL);
        assertNull("Removed item should not be in cache or data source", swiftCache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the clear operation of the cache.
     *
     * Verifies that clearing the cache removes items from the cache but not from the data source.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testClear() throws SQLException {
        SwiftCacheManager.clearTestTable();

        swiftCache.put("key1", 1, INSERT_SQL);
        swiftCache.put("key2", 2, INSERT_SQL);
        swiftCache.clear();

        assertEquals("Cache should be empty", 0, swiftCache.size());
        assertNull("Cleared item should not be in cache", swiftCache.get("key1", ""));
        assertNull("Cleared item should not be in cache", swiftCache.get("key2", ""));

        // Values should still be in the data source
        assertEquals("Value should still be in data source", 1, swiftCache.get("key1", SELECT_SQL));
        assertEquals("Value should still be in data source", 2, swiftCache.get("key2", SELECT_SQL));

        SwiftCacheManager.deleteTestTable();
    }

    /**
     * Tests the retrieval of the eviction strategy from the cache.
     *
     * Verifies that the eviction strategy is correctly retrieved from the cache.
     */
    @Test
    public void testGetEvictionStrategy() {
        IEvictionStrategy<Object, Object> evictionStrategy = swiftCache.getEvictionStrategy();
        assertNotNull("Eviction strategy should not be null", evictionStrategy);
    }

    /**
     * Tests the retrieval of the reading policy from the cache.
     *
     * Verifies that the reading policy is correctly retrieved from the cache.
     */
    @Test
    public void testGetReadingPolicy() {
        IReadingPolicy<Object, Object> readingPolicy = swiftCache.getReadingPolicy();
        assertNotNull("Reading policy should not be null", readingPolicy);
    }

    /**
     * Tests the retrieval of the writing policy from the cache.
     *
     * Verifies that the writing policy is correctly retrieved from the cache.
     */
    @Test
    public void testGetWritingPolicy() {
        IWritingPolicy<Object, Object> writingPolicy = swiftCache.getWritingPolicy();
        assertNotNull("Writing policy should not be null", writingPolicy);
    }
}
