package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public interface IWritingPolicy<K, V> {

    void write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource, String storeSql);
}
