package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

/**
 * Implements the Write-If-Absent policy for cache management.
 * <p>
 * The Write-If-Absent policy writes data to both the cache and the external data source only if the specified
 * key is not already present in the cache. This ensures that existing cache entries are not overwritten,
 * and data is only added if it is not already available.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class WriteIfAbsentPolicy<K, V> implements IWritingPolicy<K, V> {

    /**
     * Writes a value associated with the specified key to the cache and the data source if the key is not already
     * present in the cache.
     * <p>
     * This method performs the following actions if the cache does not contain the specified key:
     * <ul>
     *     <li>Inserts the key-value pair into the cache map.</li>
     *     <li>Persists the key-value pair to the external data source using the provided SQL query.</li>
     * </ul>
     * If the key is already present in the cache, no changes are made to either the cache or the data source.
     * </p>
     *
     * @param cacheMap The map representing the cache, where the key-value pair will be added if the key is absent.
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @param dataSource The data source used to store the value if the key is not present in the cache.
     * @param storeSql The SQL query to be executed to store the value in the data source.
     */
    public void write(Map<K, V> cacheMap, K key, V value, IDataSource<K, V> dataSource, String storeSql) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            dataSource.store(key, value, storeSql);
        }
    }
}
