package org.swiftcache.evictionstrategy;

import java.util.Map;
import java.util.Queue;


public interface IEvictionStrategy<K, V> {

    K evict(Map<K, V> cacheMap, Queue<K> evictionQueue);

    void access(K key, Queue<K> evictionQueue);
}
