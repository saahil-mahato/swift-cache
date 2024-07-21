package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

public class WriteAlwaysPolicy<K, V> implements IWritingPolicy<K, V> {
    public void write(Map<K, V> cacheMap, K key, V value, IDataSource<K, V> dataSource, String storeSql) {
        cacheMap.put(key, value);
        dataSource.store(key, value, storeSql);
    }
}
