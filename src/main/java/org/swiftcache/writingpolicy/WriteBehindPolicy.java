package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {

    @Override
    public void write(Map<K, V> cacheMap, final K key, final V value, final DataSource<K, V> dataSource) {
        cacheMap.put(key, value);
        new Thread(() -> dataSource.put(key, value)).start();
    }
}
