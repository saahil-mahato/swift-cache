package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

public interface IReadingPolicy<K, V> {

    V read(Map<K, V> cacheMap, K key);

    V readWithDataSource(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource, String sql);
}
