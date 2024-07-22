package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;

/**
 * Implements the Read-Through policy for cache management.
 * <p>
 * The Read-Through policy first attempts to read data from the cache. If the data is not present in the cache,
 * it fetches the data from an external data source using a provided SQL query and then updates the cache with the fetched data.
 * This ensures that the cache is populated with the latest data from the data source when a cache miss occurs.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {

    /**
     * Reads a value associated with the specified key from the cache or fetches it from an external data source.
     * <p>
     * This method first attempts to retrieve the value from the cache map. If the value is not present in the cache,
     * it fetches the value from the external data source using the provided SQL query. If the value is successfully
     * fetched from the data source, it is added to the cache map.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read or fetched.
     * @param dataSource The data source used to fetch the value if it is not present in the cache.
     * @param fetchSql The SQL query to be executed to fetch the value from the data source.
     * @return The value associated with the specified key, either from the cache or fetched from the data source.
     */
    public V readWithDataSource(Map<K, V> cacheMap, K key, IDataSource<K, V> dataSource, String fetchSql) {
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
     * Reads a value associated with the specified key from the cache.
     * <p>
     * This method is not supported in the Read-Through policy as it requires a data source to fetch values.
     * Attempting to call this method will result in an {@link UnsupportedOperationException}.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read from the cache.
     * @return This method will always throw an {@link UnsupportedOperationException}.
     * @throws UnsupportedOperationException Always thrown to indicate that the method is not supported.
     */
    public V read(Map<K, V> cacheMap, K key) {
        throw new UnsupportedOperationException("Read Through Policy requires a data source.");
    }
}
