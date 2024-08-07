package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiftcache.evictionstrategy.FIFOEvictionStrategy;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;


class FIFOEvictionStrategyTest {

    private FIFOEvictionStrategy<String, String> fifoEvictionStrategy;
    private Map<String, String> cacheMap;
    private Queue<String> evictionQueue;

    @BeforeEach
    void setUp() {
        fifoEvictionStrategy = new FIFOEvictionStrategy<>();
        cacheMap = new ConcurrentHashMap<>();
        evictionQueue = new LinkedList<>();
    }

    @Test
    void testEvictRemovesOldestItem() {
        // Setup initial state
        cacheMap.put("key1", "value1");
        evictionQueue.offer("key1");
        cacheMap.put("key2", "value2");
        evictionQueue.offer("key2");
        cacheMap.put("key3", "value3");
        evictionQueue.offer("key3");

        // Evict the oldest item
        fifoEvictionStrategy.evict(cacheMap, evictionQueue);

        // Check that the oldest item is removed
        assertFalse(cacheMap.containsKey("key1"));
        assertEquals(2, evictionQueue.size()); // Queue size should be reduced
    }

    @Test
    void testEvictDoesNotRemoveIfQueueIsEmpty() {
        // Evict from an empty queue
        fifoEvictionStrategy.evict(cacheMap, evictionQueue);

        // Check that the cache remains empty
        assertTrue(cacheMap.isEmpty());
        assertTrue(evictionQueue.isEmpty());
    }

    @Test
    void testUpdateQueueAddsKeyToEnd() {
        // Add keys to the eviction queue
        fifoEvictionStrategy.updateQueue("key1", evictionQueue);
        fifoEvictionStrategy.updateQueue("key2", evictionQueue);

        // Update the queue with a key that already exists
        fifoEvictionStrategy.updateQueue("key2", evictionQueue);

        // Check that the queue contains the keys in the correct order
        assertEquals("key1", evictionQueue.poll()); // key1 should be first
        assertEquals("key2", evictionQueue.poll()); // key2 should be second
    }
}