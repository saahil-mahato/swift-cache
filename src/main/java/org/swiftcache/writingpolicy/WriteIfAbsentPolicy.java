package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

public class WriteIfAbsentPolicy<K,V> implements IWritingPolicy<K, V> {
    public void write (Map<K, V> cacheMap, K key, V value, IDataSource<K, V> dataSource, String storeSql) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            dataSource.store(key, value, storeSql);
        }
    }
}
