package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

public class WriteIfAbsentPolicy<K,V> implements IWritingPolicy<K, V> {
    public void write (Map<String, Integer> cacheMap, String key, int value, IDataSource<String, Integer> dataSource, String storeSql) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            dataSource.store(key, value, storeSql);
        }
    }
}
