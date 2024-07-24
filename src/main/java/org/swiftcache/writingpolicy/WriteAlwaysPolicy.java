package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Implementation of the Write-Always writing policy for caching.
 * This policy writes a value to both the cache and the data source unconditionally,
 * ensuring that both the cache and the data source are updated with the new value.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class WriteAlwaysPolicy<K, V> implements IWritingPolicy<K, V> {

    /**
     * Writes a value to the cache and the data source.
     * The value is stored in the cache under the specified key and also written to the data source
     * using the provided SQL query.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key under which the value is to be stored
     * @param value      the value to be stored
     * @param dataSource the data source to be updated with the new value
     * @param storeSql   the SQL query to store the value in the data source
     */
    @Override
    public void write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource, String storeSql) {
        cacheMap.put(key, value);
        dataSource.store(key, value, storeSql);
    }
}
