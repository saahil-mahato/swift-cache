package org.saahil.cache.readingpolicy;

import java.util.Map;

public interface IReadingPolicy<K, V> {
    public interface ReadingPolicy<K, V> {
        V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource);
    }
}
