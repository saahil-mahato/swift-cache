package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the IEvictionStrategy interface that uses the
 * First-In-First-Out (FIFO) eviction policy. This strategy evicts the
 * oldest entry from the cache when the maximum size is reached.
 *
 * @param <K> the type of keys maintained by this eviction strategy
 * @param <V> the type of values maintained by this eviction strategy
 */
public class FIFOEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    private static final Logger logger = Logger.getLogger(FIFOEvictionStrategy.class.getName());

    /**
     * Evicts an entry from the cache based on the FIFO policy. The oldest
     * entry, which is at the head of the eviction queue, is removed.
     *
     * @param cacheMap the cache map containing the entries
     * @param evictionQueue the queue used for eviction
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
     * Updates the eviction queue based on the FIFO policy. If the key is not
     * already in the queue, it is added to the tail of the queue.
     *
     * @param key the key to update in the eviction queue
     * @param evictionQueue the queue to update
     */
    @Override
    public void updateQueue(K key, Queue<K> evictionQueue) {
        if (!evictionQueue.contains(key)) {
            evictionQueue.offer(key);

            logger.log(Level.INFO, "Key {0} added to eviction queue (FIFO)", key);
        }
    }
}
