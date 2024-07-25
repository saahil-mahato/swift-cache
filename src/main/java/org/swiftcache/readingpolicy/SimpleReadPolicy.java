package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {

    @Override
    public V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource) {
        // Ignore data source for simple read
        return cacheMap.get(key);
    }
}
