package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

public interface IWritingPolicy<K, V> {
    void write(Map<K, V> cacheMap, K key, V value, IDataSource<K, V> dataSource, String storeSql);
}
