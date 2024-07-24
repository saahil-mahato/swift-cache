package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {

    public V readWithDataSource(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource, String fetchSql) {
        V value = cacheMap.get(key);
        if (value == null) {
            value = dataSource.fetch(key, fetchSql);
            if (value != null) {
                cacheMap.put(key, value);
            }
        }
        return value;
    }

    public V read(Map<K, V> cacheMap, K key) {
        throw new UnsupportedOperationException("Read Through Policy requires a data source.");
    }
}
