package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {

    @Override
    public V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource) {
        V value = cacheMap.get(key);
        if (value == null) {
            value = dataSource.get(key);
            if (value != null) {
                cacheMap.put(key, value);
            }
        }
        return value;
    }
}
