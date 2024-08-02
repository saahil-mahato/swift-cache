package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Least Recently Used (LRU) eviction strategy for a cache.
 * In LRU eviction, the least recently used entry (the one that hasn't been accessed
 * for the longest time) is evicted when the cache reaches its maximum size.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class LRUEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    private static final Logger logger = Logger.getLogger(LRUEvictionStrategy.class.getName());

    /**
     * Evicts entries from the cache using the LRU strategy.
     * This method removes the least recently used entry (the first element in the eviction queue)
     * from the cache map and the queue itself.
     *
     * @param cacheMap  The cache map containing key-value pairs.
     * @param evictionQueue  The queue used to track access order for eviction (assumed to be LRU).
     */
    @Override
    public void evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        K evictedKey = evictionQueue.poll();

        if (evictedKey != null) {
            cacheMap.remove(evictedKey);

            logger.log(Level.INFO, "Key {0} evicted (FIFO)", evictedKey);
        }
    }

    /**
     * Updates the eviction queue with the accessed key.
     * This method removes the key from its current position in the queue (indicating it was used)
     * and then adds it back to the end of the queue (making it the most recently used).
     * This effectively moves the key to the front of the access order for eviction purposes.
     *
     * @param key           The key that was accessed.
     * @param evictionQueue The queue used to track access order for eviction (assumed to be LRU).
     */
    @Override
    public void updateQueue(K key, Queue<K> evictionQueue) {
        evictionQueue.remove(key); // Remove from current position
        evictionQueue.offer(key);  // Add to the end (most recently used)

        logger.log(Level.INFO, "Key {0} added to eviction queue (FIFO)", key);
    }
}
