package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import static org.junit.jupiter.api.Assertions.*;


class SwiftCacheManagerTest {

    private SwiftCacheManager<String, String> cacheManager;
    private SwiftCacheConfig config;

    @BeforeEach
    void setUp() {
        config = new SwiftCacheConfig(100, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.READ_THROUGH_POLICY,
                SwiftCacheConfig.WRITE_ALWAYS_POLICY);
        cacheManager = new SwiftCacheManager<>(config);
    }

    @Test
    void testCacheInitialization() {
        SwiftCache<String, String> swiftCache = cacheManager.getSwiftCache();

        // Verify that the cache is initialized with the correct policies
        assertNotNull(swiftCache);

        IEvictionStrategy<String, String> evictionStrategy = swiftCache.getEvictionStrategy();
        IReadingPolicy<String, String> readingPolicy = swiftCache.getReadingPolicy();
        IWritingPolicy<String, String> writingPolicy = swiftCache.getWritingPolicy();

        assertNotNull(evictionStrategy);
        assertNotNull(readingPolicy);
        assertNotNull(writingPolicy);

        assertTrue(evictionStrategy instanceof org.swiftcache.evictionstrategy.LRUEvictionStrategy);
        assertTrue(readingPolicy instanceof org.swiftcache.readingpolicy.ReadThroughPolicy);
        assertTrue(writingPolicy instanceof org.swiftcache.writingpolicy.WriteAlwaysPolicy);
    }

    @Test
    void testInvalidEvictionStrategy() {
        config = new SwiftCacheConfig(100, "INVALID_EVICTION",
                SwiftCacheConfig.READ_THROUGH_POLICY,
                SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SwiftCacheManager<>(config));

        assertEquals("Invalid eviction strategy: INVALID_EVICTION", exception.getMessage());
    }

    @Test
    void testInvalidReadingPolicy() {
        config = new SwiftCacheConfig(100, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                "INVALID_READ_POLICY",
                SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SwiftCacheManager<>(config));

        assertEquals("Invalid reading policy: INVALID_READ_POLICY", exception.getMessage());
    }

    @Test
    void testInvalidWritingPolicy() {
        config = new SwiftCacheConfig(100, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.READ_THROUGH_POLICY,
                "INVALID_WRITE_POLICY");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SwiftCacheManager<>(config));

        assertEquals("Invalid writing policy: INVALID_WRITE_POLICY", exception.getMessage());
    }
}