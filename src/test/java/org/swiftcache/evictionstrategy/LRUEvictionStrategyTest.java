package org.swiftcache.evictionstrategy;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit tests for the {@link LRUEvictionStrategy} class.
 *
 * Tests the LRU (Least Recently Used) eviction strategy for a cache.
 * Ensures that the LRU strategy correctly evicts the least recently used item
 * when eviction is performed.
 *
 * Uses JUnit for test framework.
 *
 * @see LRUEvictionStrategy
 */
public class LRUEvictionStrategyTest {

    /**
     * Tests the {@link LRUEvictionStrategy#evict} method.
     * Verifies that the LRU eviction strategy correctly evicts the least recently
     * used item from the queue and updates the queue accordingly.
     */
    @Test
    public void testEvict() {
        LRUEvictionStrategy<String, Integer> strategy = new LRUEvictionStrategy<>();
        Map<String, Integer> cacheMap = new LinkedHashMap<>();
        Queue<String> evictionQueue = new LinkedList<>();

        // Adding keys to the eviction queue
        evictionQueue.offer("key1");
        evictionQueue.offer("key2");
        evictionQueue.offer("key3");

        // Accessing a key to simulate recent usage
        strategy.access("key1", evictionQueue);

        // Performing eviction
        String evictedKey = strategy.evict(cacheMap, evictionQueue);

        // Verifying the eviction result
        assertEquals("key2", evictedKey); // The least recently used key should be evicted
        assertEquals(2, evictionQueue.size()); // The size should now be 2
        assertFalse(evictionQueue.contains("key2")); // The evicted key should not be in the queue
    }
}
