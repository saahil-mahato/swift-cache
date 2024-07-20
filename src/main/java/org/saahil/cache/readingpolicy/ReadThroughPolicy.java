package org.saahil.cache.readingpolicy;

import java.util.Map;

public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {
    public read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource) {
        V value = cacheMap.get(key);
        if (value == null) {
            value = dataSource.fetch(key);
            if (value != null) {
                cacheMap.put(key, value);
            }
        }
        return value;
    }
}
