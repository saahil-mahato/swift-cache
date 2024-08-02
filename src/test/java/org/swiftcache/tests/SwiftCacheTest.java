package org.swiftcache.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.SwiftCacheManager;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.FIFOEvictionStrategy;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.evictionstrategy.LRUEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.readingpolicy.ReadThroughPolicy;
import org.swiftcache.readingpolicy.RefreshAheadPolicy;
import org.swiftcache.writingpolicy.WriteAlwaysPolicy;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SwiftCache class.
 * This class provides comprehensive test cases to verify the behavior of the SwiftCache implementation
 * under various conditions, including different eviction strategies, read/write policies, and cache operations.
 * The tests rely on Mockito for mocking the ICacheRepository to isolate the cache's behavior.
 */
class SwiftCacheTest {

    @Mock
    private ICacheRepository<String, String> mockRepository;

    private SwiftCache<String, String> cache;

    /**
     * Initializes the test environment by creating a mock repository.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Cleans up the test environment by clearing the cache and resetting mock interactions.
     */
    @AfterEach
    @SuppressWarnings("unchecked")
    void tearDown() {
        try {
            cache.clear();
            clearInvocations(mockRepository);
        } catch (NullPointerException e) {
            // Do nothing
        }
    }

    /**
     * Initializes a SwiftCache instance with the specified configuration.
     *
     * @param capacity        The maximum size of the cache.
     * @param evictionStrategy The eviction strategy to use.
     * @param readPolicy     The read policy to use.
     * @param writePolicy    The write policy to use.
     */
    private void initializeCache(int capacity, String evictionStrategy, String readPolicy, String writePolicy) {
        SwiftCacheConfig config = new SwiftCacheConfig(capacity, evictionStrategy, readPolicy, writePolicy);
        SwiftCacheManager<String, String> cacheManager = new SwiftCacheManager<>(config, mockRepository);
        cache = cacheManager.getSwiftCache();
    }

    /**
     * Tests LRU eviction with Read-Through and Write-Always policies.
     * Verifies that the least recently used item is evicted when the cache reaches capacity.
     * Also checks the write-always behavior by verifying interactions with the mock repository.
     */
    @Test
    void testLRUEvictionWithReadThroughAndWriteAlways() {
        initializeCache(5, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.READ_THROUGH_POLICY, SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        // Test put and get
        cache.put("key0", "value0");
        assertEquals("value0", cache.get("key0"));

        // Test eviction
        for (int i = 1; i <= 5; ++i) {
            cache.put("key" + i, "value" + i);
        }
        assertNull(cache.get("key0")); // Should be evicted

        when(mockRepository.get("key0")).thenReturn("value0");
        assertEquals("value0", cache.get("key0")); // should fetch from data source.

        // test cache config
        assertEquals(LRUEvictionStrategy.class, cache.getEvictionStrategy().getClass());
        assertEquals(ReadThroughPolicy.class, cache.getReadingPolicy().getClass());
        assertEquals(WriteAlwaysPolicy.class, cache.getWritingPolicy().getClass());

        // Verify write-always behavior
        verify(mockRepository, times(6)).put(any(), any());
    }

    /**
     * Tests the LRU eviction strategy when the eviction queue is empty.
     * Verifies that no keys are evicted, resulting in a queue size of zero.
     */
    @Test
    void testLRUEmptyEvict() {
        IEvictionStrategy<String, String> evictionStrategy = new LRUEvictionStrategy<>();
        Map<String, String> tempCache = new ConcurrentHashMap<>();
        tempCache.put("key0", "value0");
        Queue<String> queue = new LinkedList<>();
        evictionStrategy.evict(tempCache, queue);
        assertEquals(0, queue.size());
    }

    /**
     * Tests the FIFO eviction strategy when the eviction queue is empty.
     * Verifies that no keys are evicted, resulting in a queue size of zero.
     */
    @Test
    void testFIFOEmptyEvict() {
        IEvictionStrategy<String, String> evictionStrategy = new FIFOEvictionStrategy<>();
        Map<String, String> tempCache = new ConcurrentHashMap<>();
        tempCache.put("key0", "value0");
        Queue<String> queue = new LinkedList<>();
        evictionStrategy.evict(tempCache, queue);
        assertEquals(0, queue.size());
    }

    /**
     * Tests FIFO eviction with Simple Read and Write-Behind policies.
     * Verifies that the first-in-first-out eviction strategy is applied correctly.
     * Also checks the asynchronous write-behind behavior.
     */
    @Test
    void testFIFOEvictionWithSimpleReadAndWriteBehind() {
        ICacheRepository<String, String> spyRepository = Mockito.spy(mockRepository);

        initializeCache(3, SwiftCacheConfig.FIFO_EVICTION_STRATEGY,
                SwiftCacheConfig.SIMPLE_READ_POLICY, SwiftCacheConfig.WRITE_BEHIND_POLICY);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4");

        assertNull(cache.get("key1")); // Should be evicted
        assertEquals("value2", cache.get("key2"));

        // Verify write-behind behavior (asynchronous, so we need to wait a bit)
        await().atMost(2, TimeUnit.SECONDS).until(() -> Mockito.mockingDetails(spyRepository).getInvocations().size() == 4);
        verify(mockRepository, times(4)).put(any(), any());
    }

    /**
     * Tests LRU eviction with Refresh-Ahead and Write-If-Absent policies.
     * Verifies the refresh-ahead behavior and the write-if-absent condition.
     */
    @Test
    void testLRUEvictionWithRefreshAheadAndWriteIfAbsent() {
        ICacheRepository<String, String> spyRepository = Mockito.spy(mockRepository);

        initializeCache(3, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.REFRESH_AHEAD_POLICY, SwiftCacheConfig.WRITE_IF_ABSENT_POLICY);

        cache.put("key1", "value1");
        cache.get("key1");

        // Verify refresh-ahead behavior
        await().atMost(2, TimeUnit.SECONDS).until(() -> Mockito.mockingDetails(spyRepository).getInvocations().size() == 2);
        verify(mockRepository, times(1)).get("key1");

        // Test write-if-absent
        cache.put("key1", "newValue1");
        verify(mockRepository, times(1)).put("key1", "value1"); // Should not write again
    }

    /**
     * Tests the Refresh-Ahead policy with new data.
     * Verifies that the cache is updated with the new value from the data source.
     */
    @Test
    void testRefreshAheadWithNewData() {
        ICacheRepository<String, String> spyRepository = Mockito.spy(mockRepository);
        when(mockRepository.get("key0")).thenReturn("value0New");

        IReadingPolicy<String, String> policy = new RefreshAheadPolicy<>();
        Map<String, String> tempMap = new ConcurrentHashMap<>();
        tempMap.put("key0", "value0");
        policy.read(tempMap, "key0", new DataSource<>(mockRepository));

        // Verify refresh-ahead behavior
        await().atMost(2, TimeUnit.SECONDS).until(
                () -> Mockito.mockingDetails(spyRepository).getInvocations().size() == 1
        );

        assertEquals("value0New", tempMap.get("key0"));
    }

    /**
     * Tests the cache size behavior under different conditions.
     */
    @Test
    void testCacheSize() {
        initializeCache(3, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.SIMPLE_READ_POLICY, SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertEquals(3, cache.size());
        cache.put("key4", "value4");
        assertEquals(3, cache.size());
    }

    /**
     * Tests the cache clear functionality.
     */
    @Test
    void testCacheClear() {
        initializeCache(5, SwiftCacheConfig.FIFO_EVICTION_STRATEGY,
                SwiftCacheConfig.SIMPLE_READ_POLICY, SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.clear();

        assertEquals(0, cache.size());
        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
    }

    /**
     * Tests the cache remove functionality.
     */
    @Test
    void testCacheRemove() {
        initializeCache(5, SwiftCacheConfig.LRU_EVICTION_STRATEGY,
                SwiftCacheConfig.READ_THROUGH_POLICY, SwiftCacheConfig.WRITE_ALWAYS_POLICY);

        cache.put("key1", "value1");
        cache.remove("key1");

        assertNull(cache.get("key1"));
        verify(mockRepository).remove("key1");
    }

    /**
     * Tests the initialization of the cache with invalid configurations.
     * Verifies that an IllegalArgumentException is thrown for each invalid parameter.
     */
    @Test
    void testInvalidCacheConfig() {
        assertThrows(IllegalArgumentException.class, () -> initializeCache(5,
                "ILLEGAL", SwiftCacheConfig.READ_THROUGH_POLICY,
                SwiftCacheConfig.WRITE_ALWAYS_POLICY));

        assertThrows(IllegalArgumentException.class, () -> initializeCache(5,
                SwiftCacheConfig.LRU_EVICTION_STRATEGY, "ILLEGAL",
                SwiftCacheConfig.WRITE_ALWAYS_POLICY));

        assertThrows(IllegalArgumentException.class, () -> initializeCache(5,
                SwiftCacheConfig.LRU_EVICTION_STRATEGY, SwiftCacheConfig.READ_THROUGH_POLICY,
                "ILLEGAL"));
    }
}
