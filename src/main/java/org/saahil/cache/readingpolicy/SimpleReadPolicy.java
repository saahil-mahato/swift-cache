package org.saahil.cache.readingpolicy;

import org.saahil.cache.datasource.IDataSource;

import java.util.Map;

public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {
    public V read(Map<K, V> cacheMap, K key) {
        return cacheMap.get(key);
    }

    public V readWithDataSource(Map<K, V> cacheMap, K key, IDataSource<K, V> dataSource, String sql) {
        throw new UnsupportedOperationException("Simple Read Policy cannot use data source.");
    }
}
