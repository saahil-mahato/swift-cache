package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Interface for defining cache eviction strategies.
 * <p>
 * This interface provides methods for determining which entry to evict from the cache and for updating
 * the eviction strategy based on access to cache entries. Implementations of this interface should
 * define specific strategies for handling cache eviction.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public interface IEvictionStrategy<K, V> {

    /**
     * Determines which entry should be evicted from the cache.
     * <p>
     * This method is called when the cache reaches its maximum capacity or when an eviction is needed.
     * The specific eviction strategy determines how the entry to be evicted is chosen.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param evictionQueue The queue that maintains the order of keys for eviction.
     * @return The key of the entry to be evicted, or null if no entry is to be evicted.
     */
    K evict(Map<K, V> cacheMap, Queue<K> evictionQueue);

    /**
     * Updates the eviction strategy based on access to a cache entry.
     * <p>
     * This method is called whenever an entry in the cache is accessed. The specific strategy may
     * modify the eviction order or update internal state based on the access.
     * </p>
     *
     * @param key The key of the entry that was accessed.
     * @param evictionQueue The queue that maintains the order of keys for eviction.
     */
    void access(K key, Queue<K> evictionQueue);
}
