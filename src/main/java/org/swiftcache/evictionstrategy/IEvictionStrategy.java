package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Interface defining the strategy for evicting entries from a cache.
 * This interface provides a way to define different eviction policies
 * for managing the size of the cache.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public interface IEvictionStrategy<K, V> {

    /**
     * Evicts entries from the cache based on the defined strategy.
     * This method is called when the cache reaches its maximum size and needs
     * to remove entries to make space for new ones.
     *
     * @param cacheMap  The cache map containing key-value pairs.
     * @param evictionQueue  The queue used to track access order for eviction.
     */
    void evict(Map<K, V> cacheMap, Queue<K> evictionQueue);

    /**
     * Updates the eviction queue based on the access of a key.
     * This method is called whenever a key is accessed in the cache.
     * The implementation should update the eviction queue to reflect the
     * most recently used entries.
     *
     * @param key           The key that was accessed.
     * @param evictionQueue The queue used to track access order for eviction.
     */
    void updateQueue(K key, Queue<K> evictionQueue);
}
