package org.saahil.cache.writingpolicy;

import javax.sql.DataSource;
import java.util.Map;

public interface IWritingPolicy<K, V> {
    void write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource);
}
