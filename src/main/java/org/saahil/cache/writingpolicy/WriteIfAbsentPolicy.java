package org.saahil.cache.writingpolicy;

import javax.sql.DataSource;
import java.util.Map;

public class WriteIfAbsentPolicy<K,V> implements IWritingPolicy<K, V> {
    public void write (Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            dataSource.store(key, value);
        }
    }
}
