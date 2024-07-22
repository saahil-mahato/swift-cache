package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

/**
 * Implements a simple read policy for cache management.
 * <p>
 * The SimpleReadPolicy provides a straightforward approach to reading data from the cache. It ignores any data source
 * provided and always attempts to retrieve values solely from the cache. If a value is present in the cache, it is
 * returned; otherwise, null is returned.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {

    /**
     * Reads a value associated with the specified key from the cache.
     * <p>
     * This method retrieves the value from the cache map if it exists. If the key is not found in the cache, it
     * returns null. This method does not consider any external data sources.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read from the cache.
     * @return The value associated with the specified key, or null if the key is not found in the cache.
     */
    public V read(Map<K, V> cacheMap, K key) {
        return cacheMap.get(key);
    }

    /**
     * Reads a value associated with the specified key from the cache, ignoring the data source.
     * <p>
     * This method retrieves the value from the cache map and ignores the provided data source and SQL query.
     * The data source is not used in this policy, and the method behaves the same way as {@link #read(Map, Object)}.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read from the cache.
     * @param dataSource The data source to fetch the value, which is ignored in this policy.
     * @param sql The SQL query to fetch the value, which is ignored in this policy.
     * @return The value associated with the specified key from the cache, or null if the key is not found.
     */
    public V readWithDataSource(Map<K, V> cacheMap, K key, IDataSource<K, V> dataSource, String sql) {
        // Ignore data source even if it is provided
        return cacheMap.get(key);
    }
}
