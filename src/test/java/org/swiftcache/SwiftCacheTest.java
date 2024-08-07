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

/**
 * Unit tests for the SwiftCache class. This class tests the functionality
 * of the SwiftCache, including adding, retrieving, evicting items, and
 * verifying the behavior of policies.
 */
@ExtendWith(MockitoExtension.class)
class SwiftCacheTest {

    private SwiftCache<String, String> cache;

    @Mock
    private ICacheRepository<String, String> repository;

    private IEvictionStrategy<String, String> evictionStrategy;
    private IWritingPolicy<String, String> writingPolicy;
    private IReadingPolicy<String, String> readingPolicy;

    /**
     * Sets up the test environment before each test case. Initializes the
     * SwiftCache with default policies.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        evictionStrategy = new LRUEvictionStrategy<>(); // Use concrete eviction strategy
        writingPolicy = new WriteAlwaysPolicy<>(); // Use concrete writing policy
        readingPolicy = new ReadThroughPolicy<>(); // Use concrete reading policy
        cache = new SwiftCache<>(5, evictionStrategy, writingPolicy, readingPolicy);
    }

    /**
     * Tests that an item can be added to the cache.
     */
    @Test
    void testPutAddsItemToCache() {
        String key = "key1";
        String value = "value1";

        String result = cache.put(repository, key, value);

        assertEquals(value, result);
        assertEquals(1, cache.size()); // Ensure the size is now 1
    }

    /**
     * Tests that a cached item can be retrieved.
     */
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

    /**
     * Tests that eviction is called when the cache reaches its maximum size.
     */
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

    /**
     * Tests that an item can be removed from the cache.
     */
    @Test
    void testRemoveRemovesItemFromCache() {
        String key = "key1";
        cache.put(repository, key, "value1");
        cache.remove(repository, key);

        assertEquals(0, cache.size());
        verify(repository).remove(key);
    }

    /**
     * Tests the executeWithCache method to ensure it interacts correctly with the repository.
     */
    @Test
    void testExecuteWithCache() {
        String key = "key1";
        String value = "value1";
        when(repository.executeWithCache(any(), eq(key), eq(value))).thenReturn(value);

        String result = cache.executeWithCache(repository, key, value, (repo, k, v) -> repo.get(k));

        assertEquals(value, result);
        verify(repository).executeWithCache(any(), eq(key), eq(value));
    }

    /**
     * Tests that the size method returns the current size of the cache.
     */
    @Test
    void testSizeReturnsCurrentSize() {
        assertEquals(0, cache.size()); // Size should be 0 initially
        cache.put(repository, "key1", "value1");
        assertEquals(1, cache.size()); // Size should be 1 after adding an item
    }

    /**
     * Tests that the clear method empties the cache.
     */
    @Test
    void testClearEmptiesCache() {
        cache.put(repository, "key1", "value1");
        cache.clear();

        assertEquals(0, cache.size());
    }

    /**
     * Tests that the getEvictionStrategy method returns the correct eviction strategy.
     */
    @Test
    void testGetEvictionStrategy() {
        assertEquals(evictionStrategy, cache.getEvictionStrategy());
    }

    /**
     * Tests that the getReadingPolicy method returns the correct reading policy.
     */
    @Test
    void testGetReadingPolicy() {
        assertEquals(readingPolicy, cache.getReadingPolicy());
    }

    /**
     * Tests that the getWritingPolicy method returns the correct writing policy.
     */
    @Test
    void testGetWritingPolicy() {
        assertEquals(writingPolicy, cache.getWritingPolicy());
    }
}
