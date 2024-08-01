package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the First-In-First-Out (FIFO) eviction strategy for a cache.
 * In FIFO eviction, the least recently used entry (the one that was added to the cache first)
 * is evicted when the cache reaches its maximum size.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class FIFOEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    private static final Logger logger = Logger.getLogger(FIFOEvictionStrategy.class.getName());

    /**
     * Evicts entries from the cache using the FIFO strategy.
     * This method removes the least recently used entry (the first element in the eviction queue)
     * from the cache map and the queue itself.
     *
     * @param cacheMap  The cache map containing key-value pairs.
     * @param evictionQueue  The queue used to track access order for eviction (assumed to be FIFO).
     */
    @Override
    public void evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        K evictedKey = evictionQueue.poll();

        if (evictedKey != null) {
            cacheMap.remove(evictedKey);
            evictionQueue.remove(evictedKey);

            logger.log(Level.INFO, "Key {} evicted (FIFO)", evictedKey);
        }
    }

    /**
     * Updates the eviction queue with the accessed key.
     * If the key is not already present in the queue, it is added to the end
     * (assuming the queue implements FIFO behavior). This effectively moves the key
     * to the back of the access order.
     *
     * @param key           The key that was accessed.
     * @param evictionQueue The queue used to track access order for eviction (assumed to be FIFO).
     */
    @Override
    public void updateQueue(K key, Queue<K> evictionQueue) {
        if (!evictionQueue.contains(key)) {
            evictionQueue.offer(key);
            logger.log(Level.INFO, "Key {} added to eviction queue (FIFO)", key);
        }
    }
}
