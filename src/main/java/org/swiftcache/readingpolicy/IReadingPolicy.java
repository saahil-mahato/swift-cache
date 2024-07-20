package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

public interface IReadingPolicy<K, V> {
    V read(Map<K, V> cacheMap, K key);
    V readWithDataSource(Map<K, V> cacheMap, K key, IDataSource<K, V> dataSource, String sql);
}
