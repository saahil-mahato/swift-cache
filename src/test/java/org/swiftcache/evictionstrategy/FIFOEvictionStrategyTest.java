package org.swiftcache.evictionstrategy;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit tests for the {@link FIFOEvictionStrategy} class.
 *
 * Tests the FIFO (First In, First Out) eviction strategy for a cache.
 * Ensures that the FIFO strategy correctly evicts the oldest item in the queue.
 *
 * Uses JUnit for test framework.
 *
 * @see FIFOEvictionStrategy
 */
public class FIFOEvictionStrategyTest {

    /**
     * Tests the {@link FIFOEvictionStrategy#evict} method.
     * Verifies that the FIFO eviction strategy correctly evicts the oldest item
     * from the queue and updates the queue accordingly.
     */
    @Test
    public void testEvict() {
        FIFOEvictionStrategy<String, Integer> strategy = new FIFOEvictionStrategy<>();
        Map<String, Integer> cacheMap = new LinkedHashMap<>();
        Queue<String> evictionQueue = new LinkedList<>();

        // Adding keys to the eviction queue
        evictionQueue.offer("key1");
        evictionQueue.offer("key2");
        evictionQueue.offer("key3");

        // Performing eviction
        String evictedKey = strategy.evict(cacheMap, evictionQueue);

        // Verifying the eviction result
        assertEquals("key1", evictedKey); // The first key added should be evicted
        assertEquals(2, evictionQueue.size()); // The size should now be 2
        assertFalse(evictionQueue.contains("key1")); // The evicted key should not be in the queue
    }
}
