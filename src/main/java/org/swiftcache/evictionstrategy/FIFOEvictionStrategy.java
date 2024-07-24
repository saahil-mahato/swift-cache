package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Implementation of the First-In-First-Out (FIFO) eviction strategy for caching.
 * This strategy evicts the oldest entry in the cache, i.e., the one that was added first.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class FIFOEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    /**
     * Determines which entry should be evicted from the cache based on FIFO strategy.
     * It evicts the entry that was added first to the cache.
     *
     * @param cacheMap       the current map of cache entries
     * @param evictionQueue the queue used to track the order of entries for eviction
     * @return the key of the entry to be evicted, or {@code null} if the queue is empty
     */
    @Override
    public K evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        return evictionQueue.poll();
    }

    /**
     * Updates the eviction strategy based on access to a specific key.
     * This method does nothing for FIFO eviction strategy as FIFO does not depend on access patterns.
     *
     * @param key            the key that was accessed
     * @param evictionQueue the queue used to track the order of entries for eviction
     */
    @Override
    public void access(K key, Queue<K> evictionQueue) {
        // Do nothing for FIFO eviction strategy
    }
}
