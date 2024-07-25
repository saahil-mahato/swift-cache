package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;


public class FIFOEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {

    @Override
    public void evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        K evictedKey = evictionQueue.poll();

        if (evictedKey != null) {
            cacheMap.remove(evictedKey);
            evictionQueue.remove(evictedKey);
        }
    }

    @Override
    public void updateQueue(K key, Queue<K> evictionQueue) {
        if (!evictionQueue.contains(key)) {
            evictionQueue.offer(key);
        }
    }
}
