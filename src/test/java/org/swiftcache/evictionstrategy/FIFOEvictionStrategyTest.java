package org.swiftcache.evictionstrategy;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link FIFOEvictionStrategy} class.
 * <p>
 * This test class verifies the behavior of the {@link FIFOEvictionStrategy} class, specifically
 * its eviction functionality. The tests ensure that the cache eviction strategy correctly removes
 * the oldest entry based on the First-In-First-Out (FIFO) principle.
 * </p>
 */
public class FIFOEvictionStrategyTest {

    /**
     * Tests the {@link FIFOEvictionStrategy#evict(Map, Queue)} method to ensure that it correctly
     * evicts the oldest entry in the FIFO queue.
     * <p>
     * This test initializes a FIFO eviction strategy, adds a few keys to the eviction queue, and
     * then verifies that the oldest key is evicted as expected. The size of the queue and the presence
     * of the evicted key are also checked to ensure correctness.
     * </p>
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
        assertEquals("key1", evictedKey);
        assertEquals(2, evictionQueue.size());
        assertFalse(evictionQueue.contains("key1"));
    }
}
