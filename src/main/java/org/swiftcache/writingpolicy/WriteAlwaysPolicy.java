package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

public class WriteAlwaysPolicy<K, V> implements IWritingPolicy<K, V> {
    public void write(Map<String, Integer> cacheMap, String key, int value, IDataSource<String, Integer> dataSource, String storeSql) {
        cacheMap.put(key, value);
        dataSource.store(key, value, storeSql);
    }
}
