package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public class WriteIfAbsentPolicy<K, V> implements IWritingPolicy<K, V> {

    @Override
    public void write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            dataSource.put(key, value);
        }
    }
}
