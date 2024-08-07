package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LRUEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    private static final Logger logger = Logger.getLogger(LRUEvictionStrategy.class.getName());

    @Override
    public void evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        K evictedKey = evictionQueue.poll();

        if (evictedKey != null) {
            cacheMap.remove(evictedKey);

            logger.log(Level.INFO, "Key {0} evicted (LRU)", evictedKey);
        }
    }
    
    @Override
    public void updateQueue(K key, Queue<K> evictionQueue) {
        evictionQueue.remove(key); // Remove from current position
        evictionQueue.offer(key);  // Add to the end (most recently used)

        logger.log(Level.INFO, "Key {0} added to eviction queue (LRU)", key);
    }
}
