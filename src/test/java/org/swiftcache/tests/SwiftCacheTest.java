package org.swiftcache.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.swiftcache.CacheRepository.ICacheRepository;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.SwiftCacheManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SwiftCacheTest {

    @Mock
    private ICacheRepository<Object, Object> mockRepository;

    private SwiftCache<Object, Object> cache;

    private SwiftCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    @SuppressWarnings("unchecked")
    void tearDown() {
        cache.clear();
        clearInvocations(mockRepository);
    }

    private void initializeCache(int capacity, String evictionStrategy, String readPolicy, String writePolicy) {
        SwiftCacheConfig config = new SwiftCacheConfig(capacity, evictionStrategy, readPolicy, writePolicy);
        cacheManager = new SwiftCacheManager(config, mockRepository);
        cache = cacheManager.getSwiftCache();
    }

    @Test
    void testLRUEvictionWithReadThroughAndWriteAlways() {
        initializeCache(5, SwiftCacheConfig.LRUEvictionStrategy,
                SwiftCacheConfig.ReadThroughPolicy, SwiftCacheConfig.WriteAlwaysPolicy);

        // Test put and get
        cache.put("key0", "value0");
        assertEquals("value0", cache.get("key0"));

        // Test eviction
        for (int i = 1; i <= 5; ++i) {
            cache.put("key" + i, "value" + i);
        }
        assertNull(cache.get("key0")); // Should be evicted

        // Verify write-always behavior
        verify(mockRepository, times(6)).put(any(), any());
    }

    @Test
    void testFIFOEvictionWithSimpleReadAndWriteBehind() {
        initializeCache(3, SwiftCacheConfig.FIFOEvictionStrategy,
                SwiftCacheConfig.SimpleReadPolicy, SwiftCacheConfig.WriteBehindPolicy);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4");

        assertNull(cache.get("key1")); // Should be evicted
        assertEquals("value2", cache.get("key2"));

        // Verify write-behind behavior (asynchronous, so we need to wait a bit)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mockRepository, times(4)).put(any(), any());
    }

    @Test
    void testLRUEvictionWithRefreshAheadAndWriteIfAbsent() {
        initializeCache(3, SwiftCacheConfig.LRUEvictionStrategy,
                SwiftCacheConfig.RefreshAheadPolicy, SwiftCacheConfig.WriteIfAbsentPolicy);

        when(mockRepository.get("key1")).thenReturn("value1");
        cache.put("key1", "value1");
        cache.get("key1");

        // Verify refresh-ahead behavior
        try {
            Thread.sleep(100); // Wait for refresh
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mockRepository, times(1)).get("key1");

        // Test write-if-absent
        cache.put("key1", "newValue1");
        verify(mockRepository, times(1)).put("key1", "value1"); // Should not write again
    }

    @Test
    void testCacheSize() {
        initializeCache(3, SwiftCacheConfig.LRUEvictionStrategy,
                SwiftCacheConfig.SimpleReadPolicy, SwiftCacheConfig.WriteAlwaysPolicy);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertEquals(3, cache.size());
        cache.put("key4", "value4");
        assertEquals(3, cache.size());
    }

    @Test
    void testCacheClear() {
        initializeCache(5, SwiftCacheConfig.FIFOEvictionStrategy,
                SwiftCacheConfig.SimpleReadPolicy, SwiftCacheConfig.WriteAlwaysPolicy);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.clear();

        assertEquals(0, cache.size());
        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
    }

    @Test
    void testCacheRemove() {
        initializeCache(5, SwiftCacheConfig.LRUEvictionStrategy,
                SwiftCacheConfig.ReadThroughPolicy, SwiftCacheConfig.WriteAlwaysPolicy);

        cache.put("key1", "value1");
        cache.remove("key1");

        assertNull(cache.get("key1"));
        verify(mockRepository).remove("key1");
    }
}
