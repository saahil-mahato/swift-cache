package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Implementation of the Read-Through reading policy for caching.
 * This policy retrieves a value from the cache if present; otherwise, it fetches the value from a data source
 * and updates the cache with the newly fetched value.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {

    /**
     * Reads a value from the cache based on the provided key. If the key is not present in the cache,
     * it fetches the value from the data source using the provided SQL query and updates the cache with
     * the newly fetched value.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key whose associated value is to be read
     * @param dataSource the data source to be used to fetch the value if not found in the cache
     * @param fetchSql   the SQL query to retrieve the value from the data source
     * @return the value associated with the specified key, or {@code null} if no value is found
     */
    @Override
    public V readWithDataSource(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource, String fetchSql) {
        V value = cacheMap.get(key);
        if (value == null) {
            value = dataSource.fetch(key, fetchSql);
            if (value != null) {
                cacheMap.put(key, value);
            }
        }
        return value;
    }

    /**
     * This method is not supported by the Read-Through policy and throws an exception.
     * Read-Through policy requires the use of {@link #readWithDataSource(Map, Object, DataSource, String)}
     * to handle reads.
     *
     * @param cacheMap the current map of cache entries
     * @param key      the key whose associated value is to be read
     * @return this method always throws {@link UnsupportedOperationException}
     * @throws UnsupportedOperationException if this method is called
     */
    @Override
    public V read(Map<K, V> cacheMap, K key) {
        throw new UnsupportedOperationException("Read Through Policy requires a data source.");
    }
}
