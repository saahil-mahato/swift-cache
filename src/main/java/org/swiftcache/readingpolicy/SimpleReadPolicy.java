package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Implementation of the Simple reading policy for caching.
 * This policy retrieves values directly from the cache and ignores the data source even if it is provided.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {

    /**
     * Reads a value from the cache based on the provided key.
     * If the key is present in the cache, the corresponding value is returned.
     * If the key is not present, {@code null} is returned.
     *
     * @param cacheMap the current map of cache entries
     * @param key      the key whose associated value is to be read
     * @return the value associated with the specified key, or {@code null} if no value is found
     */
    @Override
    public V read(Map<K, V> cacheMap, K key) {
        return cacheMap.get(key);
    }

    /**
     * Reads a value from the cache based on the provided key.
     * This method ignores the data source and SQL query even if they are provided,
     * always retrieving the value from the cache.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key whose associated value is to be read
     * @param dataSource the data source to be used (ignored in this policy)
     * @param sql        the SQL query to retrieve the value from the data source (ignored in this policy)
     * @return the value associated with the specified key, or {@code null} if no value is found
     */
    @Override
    public V readWithDataSource(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource, String sql) {
        // Ignore data source even if it is provided
        return cacheMap.get(key);
    }
}
