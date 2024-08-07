package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.evictionstrategy.LRUEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.readingpolicy.ReadThroughPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;
import org.swiftcache.writingpolicy.WriteAlwaysPolicy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwiftCacheTest {

    private SwiftCache<String, String> cache;

    @Mock
    private ICacheRepository<String, String> repository;

    private IEvictionStrategy<String, String> evictionStrategy;
    private IWritingPolicy<String, String> writingPolicy;
    private IReadingPolicy<String, String> readingPolicy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        evictionStrategy = new LRUEvictionStrategy<>(); // Use concrete eviction strategy
        writingPolicy = new WriteAlwaysPolicy<>(); // Use concrete writing policy
        readingPolicy = new ReadThroughPolicy<>(); // Use concrete reading policy
        cache = new SwiftCache<>(5, evictionStrategy, writingPolicy, readingPolicy);
    }

    @Test
    void testPutAddsItemToCache() {
        String key = "key1";
        String value = "value1";

        String result = cache.put(repository, key, value);

        assertEquals(value, result);
        assertEquals(1, cache.size()); // Ensure the size is now 1
    }

    @Test
    void testGetReturnsCachedItem() {
        String key = "key1";
        String value = "value1";

        // Put the value in the cache
        String putValue = cache.put(repository, key, value);

        // Now, get the value from the cache
        String result = cache.get(repository, key);

        // Verify that the value returned from the cache is the same as the put value
        assertEquals(putValue, result);
    }

    @Test
    void testEvictionCalledWhenCacheIsFull() {
        // Fill the cache to its maximum size
        for (int i = 0; i < 5; i++) {
            cache.put(repository, "key" + i, "value" + i);
        }

        // Add one more item to trigger eviction
        cache.put(repository, "key6", "value6");

        // Check that the size of the cache is still within the limit
        assertTrue(cache.size() <= 5); // Size should not exceed max size

        assertNull(cache.get(repository, "key0")); // key0 should be evicted
    }

    @Test
    void testRemoveRemovesItemFromCache() {
        String key = "key1";
        cache.put(repository, key, "value1");
        cache.remove(repository, key);

        assertEquals(0, cache.size());
        verify(repository).remove(key);
    }

    @Test
    void testExecuteWithCache() {
        String key = "key1";
        String value = "value1";
        when(repository.executeWithCache(any(), eq(key), eq(value))).thenReturn(value);

        String result = cache.executeWithCache(repository, key, value, (repo, k, v) -> repo.get(k));

        assertEquals(value, result);
        verify(repository).executeWithCache(any(), eq(key), eq(value));
    }

    @Test
    void testSizeReturnsCurrentSize() {
        assertEquals(0, cache.size()); // Size should be 0 initially
        cache.put(repository, "key1", "value1");
        assertEquals(1, cache.size()); // Size should be 1 after adding an item
    }

    @Test
    void testClearEmptiesCache() {
        cache.put(repository, "key1", "value1");
        cache.clear();

        assertEquals(0, cache.size());
    }

    @Test
    void testGetEvictionStrategy() {
        assertEquals(evictionStrategy, cache.getEvictionStrategy());
    }

    @Test
    void testGetReadingPolicy() {
        assertEquals(readingPolicy, cache.getReadingPolicy());
    }

    @Test
    void testGetWritingPolicy() {
        assertEquals(writingPolicy, cache.getWritingPolicy());
    }
}