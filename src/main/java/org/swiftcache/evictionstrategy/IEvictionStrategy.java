package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

/**
 * Interface for eviction strategies used by caches to determine which entry to remove when the cache reaches its maximum size.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public interface IEvictionStrategy<K, V> {

    /**
     * Determines which entry should be evicted from the cache.
     *
     * @param cacheMap       the current map of cache entries
     * @param evictionQueue the queue used to track the order of entries for eviction
     * @return the key of the entry to be evicted, or {@code null} if no entry should be evicted
     */
    K evict(Map<K, V> cacheMap, Queue<K> evictionQueue);

    /**
     * Updates the eviction strategy based on the access to a specific key.
     *
     * @param key            the key that was accessed
     * @param evictionQueue the queue used to track the order of entries for eviction
     */
    void access(K key, Queue<K> evictionQueue);
}
