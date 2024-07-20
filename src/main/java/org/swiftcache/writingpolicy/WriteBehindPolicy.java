package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {
    public void write(Map<K, V> cacheMap, final K key, final V value, final IDataSource<K, V> dataSource, final String storeSql) {
        cacheMap.put(key, value);
        new Thread(new Runnable() {
            public void run() {
                dataSource.store(key, value, storeSql);
            }
        }).start();
    }
}
