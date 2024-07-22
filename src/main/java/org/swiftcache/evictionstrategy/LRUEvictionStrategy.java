package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Implements the Least Recently Used (LRU) eviction strategy for cache management.
 * <p>
 * The LRU eviction strategy removes the least recently used entry from the cache when eviction is required.
 * The entry that has not been accessed for the longest time is removed first. This strategy updates the order
 * of entries in the queue based on access, ensuring that the most recently accessed entries are kept in the cache.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class LRUEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    /**
     * Removes and returns the least recently used entry from the cache.
     * <p>
     * The least recently used entry is determined by the order of keys in the {@link Queue}.
     * This method uses {@link Queue#poll()} to retrieve and remove the oldest key from the queue, which represents
     * the least recently used entry.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param evictionQueue The queue that maintains the order of keys for eviction.
     * @return The key of the entry to be evicted, or null if the queue is empty.
     */
    public K evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        return evictionQueue.poll();
    }

    /**
     * Updates the eviction queue when an entry is accessed.
     * <p>
     * When an entry is accessed, it is removed from its current position in the queue and re-added to the end of the
     * queue. This operation moves the accessed entry to the most recent position, ensuring that it is less likely
     * to be evicted compared to other entries.
     * </p>
     *
     * @param key The key of the entry that was accessed.
     * @param evictionQueue The queue that maintains the order of keys for eviction.
     */
    public void access(K key, Queue<K> evictionQueue) {
        evictionQueue.remove(key); // Remove the key from its current position in the queue.
        evictionQueue.offer(key);  // Re-add the key to the end of the queue, marking it as most recently used.
    }
}
