package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Interface representing an eviction strategy for a cache, providing methods
 * to evict entries based on a specific policy and update the eviction queue.
 *
 * @param <K> the type of keys maintained by this eviction strategy
 * @param <V> the type of values maintained by this eviction strategy
 */
public interface IEvictionStrategy<K, V> {

    /**
     * Evicts entries from the cache based on the specific eviction policy
     * implemented by the strategy.
     *
     * @param cacheMap the cache map containing the entries
     * @param evictionQueue the queue used for eviction
     */
    void evict(Map<K, V> cacheMap, Queue<K> evictionQueue);

    /**
     * Updates the eviction queue based on the specific eviction policy
     * implemented by the strategy.
     *
     * @param key the key to update in the eviction queue
     * @param evictionQueue the queue to update
     */
    void updateQueue(K key, Queue<K> evictionQueue);
}
