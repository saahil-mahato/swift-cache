package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

/**
 * Interface for defining cache writing policies.
 * <p>
 * This interface specifies a method for writing data to a cache and, if necessary, persisting it to an external
 * data source. Implementations of this interface define how data should be written to both the cache and the
 * data source when changes are made.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public interface IWritingPolicy<K, V> {

    /**
     * Writes a value associated with the specified key to the cache and optionally to an external data source.
     * <p>
     * This method updates the cache with the provided key-value pair and, if required, also persists the value
     * to the external data source using the provided SQL query. Implementations can define how to handle the
     * synchronization between the cache and data source.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @param dataSource The data source used to store the value if persistence is required.
     * @param storeSql The SQL query to be executed to store the value in the data source.
     */
    void write(Map<K, V> cacheMap, K key, V value, IDataSource<K, V> dataSource, String storeSql);
}
