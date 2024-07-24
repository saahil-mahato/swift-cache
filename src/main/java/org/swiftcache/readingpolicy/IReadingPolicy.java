package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Interface for reading policies used by caches to retrieve values.
 * Defines methods for reading values from the cache or from a data source if the value is not found in the cache.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public interface IReadingPolicy<K, V> {

    /**
     * Reads a value from the cache based on the provided key.
     * If the key is present in the cache, the corresponding value is returned.
     * If the key is not present, {@code null} is returned.
     *
     * @param cacheMap the current map of cache entries
     * @param key      the key whose associated value is to be read
     * @return the value associated with the specified key, or {@code null} if no value is found
     */
    V read(Map<K, V> cacheMap, K key);

    /**
     * Reads a value from the cache based on the provided key. If the key is not present in the cache,
     * it attempts to retrieve the value from a data source using the provided SQL query.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key whose associated value is to be read
     * @param dataSource the data source to be used to fetch the value if not found in the cache
     * @param sql        the SQL query to retrieve the value from the data source
     * @return the value associated with the specified key, or {@code null} if no value is found
     */
    V readWithDataSource(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource, String sql);
}
