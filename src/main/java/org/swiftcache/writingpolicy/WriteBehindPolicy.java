package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {

    public void write(Map<K, V> cacheMap, final K key, final V value, final DataSource<K, V> dataSource, final String storeSql) {
        cacheMap.put(key, value);
        new Thread(() -> dataSource.store(key, value, storeSql)).start();
    }
}
