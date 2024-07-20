package org.saahil.cache.readingpolicy;

import java.util.Map;

public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {
    public V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource) {
        return cacheMap.get(key);
    }
}
