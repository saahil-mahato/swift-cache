package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

/**
 * Interface for defining cache reading policies.
 * <p>
 * This interface specifies methods for reading data from a cache. Implementations of this interface
 * define how data is read from the cache and, if necessary, how to fetch data from an external data source
 * when it is not present in the cache.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public interface IReadingPolicy<K, V> {

    /**
     * Reads a value associated with the specified key from the cache.
     * <p>
     * This method retrieves the value from the cache map if it exists.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read from the cache.
     * @return The value associated with the specified key, or null if the key is not found in the cache.
     */
    V read(Map<K, V> cacheMap, K key);

    /**
     * Reads a value associated with the specified key from the cache or fetches it from an external data source.
     * <p>
     * This method first attempts to retrieve the value from the cache map. If the value is not present in the
     * cache, it fetches the value from the external data source using the provided SQL query.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read or fetched.
     * @param dataSource The data source used to fetch the value if it is not present in the cache.
     * @param sql The SQL query to be executed to fetch the value from the data source.
     * @return The value associated with the specified key, either from the cache or fetched from the data source.
     */
    V readWithDataSource(Map<K, V> cacheMap, K key, IDataSource<K, V> dataSource, String sql);
}
