package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Implementation of the Write-If-Absent writing policy for caching.
 * This policy writes a value to the cache and data source only if the key is not already present in the cache.
 * It ensures that existing cache entries are not overwritten.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class WriteIfAbsentPolicy<K, V> implements IWritingPolicy<K, V> {

    /**
     * Writes a value to the cache and the data source if the specified key is not already present in the cache.
     * If the key is absent, the value is added to both the cache and the data source using the provided SQL query.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key under which the value is to be stored
     * @param value      the value to be stored
     * @param dataSource the data source to be updated with the new value
     * @param storeSql   the SQL query to store the value in the data source
     */
    @Override
    public void write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource, String storeSql) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            dataSource.store(key, value, storeSql);
        }
    }
}
