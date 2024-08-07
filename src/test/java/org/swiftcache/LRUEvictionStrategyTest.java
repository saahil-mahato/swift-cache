package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swiftcache.evictionstrategy.LRUEvictionStrategy;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LRUEvictionStrategy class. This class tests the behavior
 * of the LRU eviction strategy, ensuring that it correctly evicts the least
 * recently used items and manages the eviction queue.
 */
class LRUEvictionStrategyTest {

    private LRUEvictionStrategy<String, String> lruEvictionStrategy;
    private Map<String, String> cacheMap;
    private Queue<String> evictionQueue;

    /**
     * Sets up the test environment before each test case. Initializes the
     * LRU eviction strategy, cache map, and eviction queue.
     */
    @BeforeEach
    void setUp() {
        lruEvictionStrategy = new LRUEvictionStrategy<>();
        cacheMap = new ConcurrentHashMap<>();
        evictionQueue = new LinkedList<>();
    }

    /**
     * Tests that the evict method removes the least recently used item from the cache
     * and the eviction queue.
     */
    @Test
    void testEvictRemovesLeastRecentlyUsedItem() {
        // Setup initial state
        cacheMap.put("key1", "value1");
        evictionQueue.offer("key1");
        cacheMap.put("key2", "value2");
        evictionQueue.offer("key2");
        cacheMap.put("key3", "value3");
        evictionQueue.offer("key3");

        // Evict the least recently used item
        lruEvictionStrategy.evict(cacheMap, evictionQueue);

        // Check that the least recently used item is removed
        assertFalse(cacheMap.containsKey("key1")); // key1 should be evicted
        assertEquals(2, evictionQueue.size()); // Queue size should be reduced
    }

    /**
     * Tests that the evict method does not remove any items if the eviction
     * queue is empty.
     */
    @Test
    void testEvictDoesNotRemoveIfQueueIsEmpty() {
        // Evict from an empty queue
        lruEvictionStrategy.evict(cacheMap, evictionQueue);

        // Check that the cache remains empty
        assertTrue(cacheMap.isEmpty());
        assertTrue(evictionQueue.isEmpty());
    }

    /**
     * Tests that the updateQueue method moves a key to the end of the eviction
     * queue, maintaining the correct order for least recently used eviction.
     */
    @Test
    void testUpdateQueueMovesKeyToEnd() {
        // Add keys to the eviction queue
        lruEvictionStrategy.updateQueue("key1", evictionQueue);
        lruEvictionStrategy.updateQueue("key2", evictionQueue);

        // Update the queue with a key that already exists
        lruEvictionStrategy.updateQueue("key1", evictionQueue);

        // Check that the queue contains the keys in the correct order
        assertEquals("key2", evictionQueue.poll()); // key2 should be first
        assertEquals("key1", evictionQueue.poll()); // key1 should be second
    }
}
