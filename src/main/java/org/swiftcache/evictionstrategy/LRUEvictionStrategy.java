package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the IEvictionStrategy interface that uses the
 * Least Recently Used (LRU) eviction policy. This strategy evicts the
 * least recently accessed entry from the cache when the maximum size is reached.
 *
 * @param <K> the type of keys maintained by this eviction strategy
 * @param <V> the type of values maintained by this eviction strategy
 */
public class LRUEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    private static final Logger logger = Logger.getLogger(LRUEvictionStrategy.class.getName());

    /**
     * Evicts an entry from the cache based on the LRU policy. The least recently
     * used entry, which is at the head of the eviction queue, is removed.
     *
     * @param cacheMap the cache map containing the entries
     * @param evictionQueue the queue used for eviction
     */
    @Override
    public void evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        K evictedKey = evictionQueue.poll();

        if (evictedKey != null) {
            cacheMap.remove(evictedKey);

            logger.log(Level.INFO, "Key {0} evicted (LRU)", evictedKey);
        }
    }

    /**
     * Updates the eviction queue based on the LRU policy. The key is removed from
     * its current position and added to the end of the queue, marking it as the
     * most recently used entry.
     *
     * @param key the key to update in the eviction queue
     * @param evictionQueue the queue to update
     */
    @Override
    public void updateQueue(K key, Queue<K> evictionQueue) {
        evictionQueue.remove(key); // Remove from current position
        evictionQueue.offer(key);  // Add to the end (most recently used)

        logger.log(Level.INFO, "Key {0} added to eviction queue (LRU)", key);
    }
}
