package org.swiftcache;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.cache.Cache;
import org.swiftcache.cache.CacheConfig;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import java.sql.SQLException;

import static org.junit.Assert.*;


public class CacheManagerTest {

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    private Cache<Object, Object> cache;
    private CacheConfig cacheConfig;

    @Before
    public void setUp() throws SQLException {
        // Initialize CacheConfig with test settings
        cacheConfig = new CacheConfig(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", // Use in-memory H2 database
                "sa",                                   // Default username for H2
                "",                                     // No password
                100,                                    // Max cache size
                "LRU",                                  // Eviction strategy
                "ReadThrough",                          // Reading policy
                "WriteAlways"                           // Writing policy
        );

        // Get cache instance using the config
        cache = CacheManager.getCache(cacheConfig);

        CacheManager.createTestTable();
    }

    @Test
    public void testPutAndGet() throws SQLException {
        CacheManager.clearTestTable();

        cache.put("key1", 1, INSERT_SQL);
        assertEquals("Value should be retrieved from cache", 1, cache.get("key1", SELECT_SQL));
    }

    @Test
    public void testEvictionStrategy() throws SQLException {
        CacheManager.clearTestTable();

        for (int i = 1; i <= cacheConfig.getMaxSize(); ++i) {
            cache.put("key" + i, i, INSERT_SQL);
        }

        // Access some keys to change their "recently used" status
        cache.get("key1", SELECT_SQL);
        cache.get("key2", SELECT_SQL);
        cache.get("key3", SELECT_SQL);
        cache.get("key4", SELECT_SQL);

        // Add new data to evict the least used data
        cache.put("key" + (cacheConfig.getMaxSize() + 1), cacheConfig.getMaxSize() + 1, INSERT_SQL);

        // The least recently used key should have been evicted
        assertNull("Least recently used key should be evicted", cache.get("key0", SELECT_SQL));

        // Recently accessed keys should still be in the cache
        assertEquals("Recently accessed key should be in cache", 1, cache.get("key1", SELECT_SQL));
        assertEquals("Recently accessed key should be in cache", 2, cache.get("key2", SELECT_SQL));
        assertEquals("Recently accessed key should be in cache", 3, cache.get("key3", SELECT_SQL));
        assertEquals("Recently accessed key should be in cache", 4, cache.get("key4", SELECT_SQL));
    }

    @Test
    public void testReadingPolicy() throws SQLException {
        CacheManager.clearTestTable();

        cache.put("key1", 1, INSERT_SQL);
        assertEquals("Value should be retrieved from cache", 1, cache.get("key1", SELECT_SQL));

        // Clear the cache to force a read from the data source
        cache.clear();
        // This should trigger a read-through to the data source
        assertEquals("Value should be retrieved from data source", 1, cache.get("key1", SELECT_SQL));
    }

    @Test
    public void testWritingPolicy() throws SQLException {
        CacheManager.clearTestTable();

        cache.put("key1", 1, INSERT_SQL);
        assertEquals("Value should be in both cache and data source", 1, cache.get("key1", SELECT_SQL));

        // Clear the cache to force a read from the data source
        cache.clear();
        assertEquals("Value should still be retrievable from data source", 1, cache.get("key1", SELECT_SQL));
    }

    @Test
    public void testRemove() throws SQLException {
        CacheManager.clearTestTable();

        cache.put("key1", 1, INSERT_SQL);
        cache.remove("key1", DELETE_SQL);
        assertNull("Removed item should not be in cache or data source", cache.get("key1", SELECT_SQL));
    }

    @Test
    public void testClear() throws SQLException {
        CacheManager.clearTestTable();

        cache.put("key1", 1, INSERT_SQL);
        cache.put("key2", 2, INSERT_SQL);
        cache.clear();

        assertEquals("Cache should be empty", 0, cache.size());
        assertNull("Cleared item should not be in cache", cache.get("key1", ""));
        assertNull("Cleared item should not be in cache", cache.get("key2", ""));

        // Values should still be in the data source
        assertEquals("Value should still be in data source", 1, cache.get("key1", SELECT_SQL));
        assertEquals("Value should still be in data source", 2, cache.get("key2", SELECT_SQL));

        CacheManager.deleteTestTable();
    }

    @Test
    public void testGetEvictionStrategy() {
        IEvictionStrategy<Object, Object> evictionStrategy = cache.getEvictionStrategy();
        assertNotNull("Eviction strategy should not be null", evictionStrategy);
    }

    @Test
    public void testGetReadingPolicy() {
        IReadingPolicy<Object, Object> readingPolicy = cache.getReadingPolicy();
        assertNotNull("Reading policy should not be null", readingPolicy);
    }

    @Test
    public void testGetWritingPolicy() {
        IWritingPolicy<Object, Object> writingPolicy = cache.getWritingPolicy();
        assertNotNull("Writing policy should not be null", writingPolicy);
    }
}
