package org.saahil.cache.evictionstrategy;

import java.util.Map;
import java.util.Queue;

public class LRUEvictionStrategy<K, V> implements IEvictionStrategy<K, V> {
    public K evict(Map<K, V> cacheMap, Queue<K> evictionQueue) {
        return evictionQueue.poll();
    }
}
