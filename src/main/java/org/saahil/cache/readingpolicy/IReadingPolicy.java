package org.saahil.cache.readingpolicy;

import javax.sql.DataSource;

public interface IReadingPolicy {
    public interface ReadingPolicy<K, V> {
        V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource);
    }
}
