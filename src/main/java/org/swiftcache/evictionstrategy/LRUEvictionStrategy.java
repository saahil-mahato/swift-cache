package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Implementation of the Least Recently Used (LRU) eviction strategy for caching.
 * This strategy evicts the least recently used entry in the cache, i.e., the entry that was accessed least recently.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class LRUEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    /**
     * Determines which entry should be evicted from the cache based on LRU strategy.
     * It evicts the least recently used entry, which is the one that was added first and not recently accessed.
     *
     * @param cacheMap       the current map of cache entries
     * @param evictionQueue the queue used to track the order of entries for eviction, with the least recently used entry at the head
     * @return the key of the entry to be evicted, or {@code null} if the queue is empty
     */
    @Override
    public K evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        return evictionQueue.poll();
    }

    /**
     * Updates the eviction strategy based on access to a specific key.
     * Moves the accessed key to the end of the queue to mark it as recently used.
     *
     * @param key            the key that was accessed
     * @param evictionQueue the queue used to track the order of entries for eviction, with the least recently used entry at the head
     */
    @Override
    public void access(K key, Queue<K> evictionQueue) {
        evictionQueue.remove(key);
        evictionQueue.offer(key);
    }
}
