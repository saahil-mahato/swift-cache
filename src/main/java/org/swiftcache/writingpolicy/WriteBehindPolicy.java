package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

/**
 * Implements the Write-Behind policy for cache management.
 * <p>
 * The Write-Behind policy ensures that write operations update the cache immediately, while persisting the data
 * to the external data source asynchronously. This approach can improve performance by deferring the data store
 * operation to a background thread, reducing the latency of write operations from the perspective of the application.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {

    /**
     * Writes a value associated with the specified key to the cache and schedules an asynchronous task to
     * persist it to the external data source.
     * <p>
     * This method performs two actions:
     * <ul>
     *     <li>Updates the cache map by associating the specified key with the given value.</li>
     *     <li>Schedules a background thread to persist the value to the data source using the provided SQL query.</li>
     * </ul>
     * The asynchronous task allows the write operation to return immediately while the data is stored in the data
     * source in the background.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @param dataSource The data source used to store the value asynchronously.
     * @param storeSql The SQL query to be executed to store the value in the data source.
     */
    public void write(Map<K, V> cacheMap, final K key, final V value, final IDataSource<K, V> dataSource, final String storeSql) {
        cacheMap.put(key, value);
        new Thread(new Runnable() {
            public void run() {
                dataSource.store(key, value, storeSql);
            }
        }).start();
    }
}
