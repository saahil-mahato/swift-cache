package org.saahil.cache.readingpolicy;

import org.saahil.cache.datasource.IDataSource;

import java.util.Map;

public interface IReadingPolicy<K, V> {
    V read(Map<K, V> cacheMap, K key);
    V readWithDataSource(Map<K, V> cacheMap, K key, IDataSource<K, V> dataSource, String sql);
}
