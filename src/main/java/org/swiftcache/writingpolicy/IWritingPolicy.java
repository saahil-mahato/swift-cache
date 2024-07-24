package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Interface for writing policies used by caches to store values.
 * Defines a method for writing values to both the cache and a data source.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public interface IWritingPolicy<K, V> {

    /**
     * Writes a value to the cache and the data source based on the provided key.
     * The method is responsible for ensuring that the cache and the data source are both updated with the new value.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key under which the value is to be stored
     * @param value      the value to be stored
     * @param dataSource the data source to be updated with the new value
     * @param storeSql   the SQL query to store the value in the data source
     */
    void write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource, String storeSql);
}
