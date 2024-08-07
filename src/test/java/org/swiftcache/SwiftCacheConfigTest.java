package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import static org.junit.jupiter.api.Assertions.*;


class SwiftCacheConfigTest {

    private SwiftCacheConfig config;

    @BeforeEach
    void setUp() {
        // Initialize with default values for tests
        config = new SwiftCacheConfig(100, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.READ_THROUGH_POLICY,
                SwiftCacheConfig.WRITE_ALWAYS_POLICY);
    }

    @Test
    void testValidCombinations() {
        // Define all valid combinations of policies
        String[] evictionStrategies = {SwiftCacheConfig.LRU_EVICTION_STRATEGY, SwiftCacheConfig.FIFO_EVICTION_STRATEGY};
        String[] readingPolicies = {SwiftCacheConfig.READ_THROUGH_POLICY, SwiftCacheConfig.SIMPLE_READ_POLICY, SwiftCacheConfig.REFRESH_AHEAD_POLICY};
        String[] writingPolicies = {SwiftCacheConfig.WRITE_ALWAYS_POLICY, SwiftCacheConfig.WRITE_BEHIND_POLICY, SwiftCacheConfig.WRITE_IF_ABSENT_POLICY};

        for (String eviction : evictionStrategies) {
            for (String read : readingPolicies) {
                for (String write : writingPolicies) {
                    config = new SwiftCacheConfig(100, eviction, read, write);
                    SwiftCacheManager<String, String> cacheManager = new SwiftCacheManager<>(config);
                    SwiftCache<String, String> swiftCache = cacheManager.getSwiftCache();

                    // Verify that the cache is initialized with the correct policies
                    assertNotNull(swiftCache);

                    IEvictionStrategy<String, String> evictionStrategy = swiftCache.getEvictionStrategy();
                    IReadingPolicy<String, String> readingPolicy = swiftCache.getReadingPolicy();
                    IWritingPolicy<String, String> writingPolicy = swiftCache.getWritingPolicy();

                    assertNotNull(evictionStrategy);
                    assertNotNull(readingPolicy);
                    assertNotNull(writingPolicy);
                }
            }
        }
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
