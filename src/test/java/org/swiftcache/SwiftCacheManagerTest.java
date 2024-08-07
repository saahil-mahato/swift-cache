package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SwiftCacheManager class. This class tests the initialization
 * of the SwiftCache with various policies and verifies the handling of invalid
 * configurations.
 */
class SwiftCacheManagerTest {

    private SwiftCacheManager<String, String> cacheManager;
    private SwiftCacheConfig config;

    /**
     * Sets up the test environment before each test case. Initializes the
     * SwiftCacheConfig and SwiftCacheManager with default values.
     */
    @BeforeEach
    void setUp() {
        config = new SwiftCacheConfig(100, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.READ_THROUGH_POLICY,
                SwiftCacheConfig.WRITE_ALWAYS_POLICY);
        cacheManager = new SwiftCacheManager<>(config);
    }

    /**
     * Tests that the cache is initialized correctly with the specified eviction,
     * reading, and writing policies.
     */
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

    /**
     * Tests that an invalid eviction strategy throws an IllegalArgumentException.
     */
    @Test
    void testInvalidEvictionStrategy() {
        config = new SwiftCacheConfig(100, "INVALID_EVICTION",
                SwiftCacheConfig.READ_THROUGH_POLICY,
                SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SwiftCacheManager<>(config));

        assertEquals("Invalid eviction strategy: INVALID_EVICTION", exception.getMessage());
    }

    /**
     * Tests that an invalid reading policy throws an IllegalArgumentException.
     */
    @Test
    void testInvalidReadingPolicy() {
        config = new SwiftCacheConfig(100, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                "INVALID_READ_POLICY",
                SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SwiftCacheManager<>(config));

        assertEquals("Invalid reading policy: INVALID_READ_POLICY", exception.getMessage());
    }

    /**
     * Tests that an invalid writing policy throws an IllegalArgumentException.
     */
    @Test
    void testInvalidWritingPolicy() {
        config = new SwiftCacheConfig(100, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.READ_THROUGH_POLICY,
                "INVALID_WRITE_POLICY");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SwiftCacheManager<>(config));

        assertEquals("Invalid writing policy: INVALID_WRITE_POLICY", exception.getMessage());
    }
}
