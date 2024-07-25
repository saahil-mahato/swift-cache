package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {

    @Override
    public V read(final Map<K, V> cacheMap, final K key, final DataSource<K, V> dataSource) {
        V value = cacheMap.get(key);

        new Thread(() -> {
                V freshValue = dataSource.get(key);
                cacheMap.put(key, freshValue);
            }
        ).start();

        return value;
    }
}
