package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

/**
 * Implements the Write-Always policy for cache management.
 * <p>
 * The Write-Always policy ensures that every write operation updates both the cache and the external data source.
 * When a value is written to the cache, it is also immediately persisted to the data source, ensuring that both
 * cache and data source are kept in sync.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class WriteAlwaysPolicy<K, V> implements IWritingPolicy<K, V> {

    /**
     * Writes a value associated with the specified key to the cache and also persists it to the external data source.
     * <p>
     * This method performs two actions:
     * <ul>
     *     <li>Updates the cache map by associating the specified key with the given value.</li>
     *     <li>Persists the value to the external data source using the provided SQL query.</li>
     * </ul>
     * This ensures that the cache and data source are always synchronized with the latest data.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @param dataSource The data source used to store the value.
     * @param storeSql The SQL query to be executed to store the value in the data source.
     */
    public void write(Map<K, V> cacheMap, K key, V value, IDataSource<K, V> dataSource, String storeSql) {
        cacheMap.put(key, value);
        dataSource.store(key, value, storeSql);
    }
}
