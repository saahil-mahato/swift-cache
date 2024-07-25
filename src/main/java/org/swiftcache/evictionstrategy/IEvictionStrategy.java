package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;


public interface IEvictionStrategy<K, V> {

    void evict(Map<K, V> cacheMap, Queue<K> evictionQueue);

    void updateQueue(K key, Queue<K> evictionQueue);
}
