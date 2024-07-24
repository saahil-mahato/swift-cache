package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {

    public V read(Map<K, V> cacheMap, K key) {
        return cacheMap.get(key);
    }

    public V readWithDataSource(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource, String sql) {
        // Ignore data source even if it is provided
        return cacheMap.get(key);
    }
}
