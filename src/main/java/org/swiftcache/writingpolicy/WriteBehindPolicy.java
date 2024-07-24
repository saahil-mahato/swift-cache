package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Implementation of the Write-Behind writing policy for caching.
 * This policy writes a value to the cache immediately but performs the write to the data source asynchronously
 * in a separate thread. This approach allows for non-blocking cache operations.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {

    /**
     * Writes a value to the cache and schedules an asynchronous task to store the value in the data source.
     * The value is stored in the cache immediately, and a new thread is started to perform the data source update
     * using the provided SQL query.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key under which the value is to be stored
     * @param value      the value to be stored
     * @param dataSource the data source to be updated with the new value
     * @param storeSql   the SQL query to store the value in the data source
     */
    @Override
    public void write(Map<K, V> cacheMap, final K key, final V value, final DataSource<K, V> dataSource, final String storeSql) {
        cacheMap.put(key, value);
        new Thread(() -> dataSource.store(key, value, storeSql)).start();
    }
}
