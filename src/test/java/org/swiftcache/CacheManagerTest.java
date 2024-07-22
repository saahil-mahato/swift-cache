package org.swiftcache;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.cache.Cache;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import static org.junit.Assert.*;

/**
 * Test class for the CacheManager.
 */
public class CacheManagerTest {

    // No need for database test here as the db operations are already tested.
    private static final String INSERT_SQL = "";
    private static final String SELECT_SQL = "";
    private static final String DELETE_SQL = "";

    private Cache<Object, Object> cache;

    /**
     * Sets up the cache before each test.
     */
    @Before
    public void setUp() {
        cache = CacheManager.getCache();
    }

    /**
     * Tests the put and get functionality of the cache.
     */
    @Test
    public void testPutAndGet() {
        cache.put("key1", "value1", INSERT_SQL);
        assertEquals("value1", cache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the eviction strategy of the cache.
     */
    @Test
    public void testEvictionStrategy() {
        for (int i = 0; i < cache.getMaxSize() + 1; ++i) {
            cache.put("key" + i , "value" + i, INSERT_SQL);
        }
        cache.put("key1", "value1", INSERT_SQL);
        cache.put("key2", "value2", INSERT_SQL);
        cache.put("key3", "value3", INSERT_SQL);
        cache.put("key4", "value4", INSERT_SQL);

        assertNull(cache.get("key0", SELECT_SQL));
        assertEquals("value1", cache.get("key1", SELECT_SQL));
        assertEquals("value2", cache.get("key2", SELECT_SQL));
        assertEquals("value3", cache.get("key3", SELECT_SQL));
        assertEquals("value4", cache.get("key4", SELECT_SQL));
    }

    /**
     * Tests the reading policy of the cache.
     */
    @Test
    public void testReadingPolicy() {
        cache.put("key1", "value1", INSERT_SQL);
        assertEquals("value1", cache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the writing policy of the cache.
     */
    @Test
    public void testWritingPolicy() {
        cache.put("key1", "value1", INSERT_SQL);
        assertEquals("value1", cache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the remove functionality of the cache.
     */
    @Test
    public void testRemove() {
        cache.put("key1", "value1", INSERT_SQL);
        cache.remove("key1", DELETE_SQL);
        assertNull(cache.get("key1", SELECT_SQL));
    }

    /**
     * Tests the clear functionality of the cache.
     */
    @Test
    public void testClear() {
        cache.put("key1", "value1", INSERT_SQL);
        cache.put("key2", "value2", INSERT_SQL);
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get("key1", SELECT_SQL));
        assertNull(cache.get("key2", SELECT_SQL));
    }

    /**
     * Tests the retrieval of the eviction strategy.
     */
    @Test
    public void testGetEvictionStrategy() {
        IEvictionStrategy<Object, Object> evictionStrategy = cache.getEvictionStrategy();
        assertNotNull(evictionStrategy);
    }

    /**
     * Tests the retrieval of the reading policy.
     */
    @Test
    public void testGetReadingPolicy() {
        IReadingPolicy<Object, Object> readingPolicy = cache.getReadingPolicy();
        assertNotNull(readingPolicy);
    }

    /**
     * Tests the retrieval of the writing policy.
     */
    @Test
    public void testGetWritingPolicy() {
        IWritingPolicy<Object, Object> writingPolicy = cache.getWritingPolicy();
        assertNotNull(writingPolicy);
    }
}
