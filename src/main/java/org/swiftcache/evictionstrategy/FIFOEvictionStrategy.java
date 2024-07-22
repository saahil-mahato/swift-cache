package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Implements the First-In-First-Out (FIFO) eviction strategy for cache management.
 * <p>
 * The FIFO eviction strategy removes the oldest entry in the cache when eviction is required.
 * Entries are removed from the front of the queue, which maintains the order of their insertion.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class FIFOEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    /**
     * Removes and returns the oldest entry from the cache.
     * <p>
     * The oldest entry is determined by the order of keys in the {@link Queue},
     * which represents the order of insertion. This method uses {@link Queue#poll()}
     * to retrieve and remove the oldest key from the queue.
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
     * For the FIFO eviction strategy, this method does not perform any action
     * when an entry is accessed, as the FIFO strategy does not modify the
     * eviction order based on access.
     * </p>
     *
     * @param key The key of the entry that was accessed.
     * @param evictionQueue The queue that maintains the order of keys for eviction.
     */
    public void access(K key, Queue<K> evictionQueue) {
        // Do nothing for FIFO eviction strategy
    }
}
