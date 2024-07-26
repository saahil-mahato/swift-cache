package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * This class implements the Simple Read reading policy for a cache.
 * In Simple Read policy, the cache is the only source for data. The data source is
 * not consulted for reads. This policy is useful when the data in the cache is
 * always up-to-date and there's no need to fetch it from an external source.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {

    @Override
    public V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource) {
        // This policy only reads from the cache, ignoring the data source.
        return cacheMap.get(key);
    }
}
