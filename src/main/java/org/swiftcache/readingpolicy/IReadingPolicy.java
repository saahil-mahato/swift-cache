package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;


/**
 * Interface defining the strategy for reading entries from a cache.
 * This interface allows for different policies to be implemented for reading data,
 * providing flexibility in how the cache interacts with the underlying data source.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public interface IReadingPolicy<K, V> {

    /**
     * Reads an entry from the cache or data source based on the policy.
     * This method defines how the reading policy retrieves a value for a given key.
     * The implementation might check the cache first, and if not found, fetch it from
     * the data source and potentially store it in the cache.
     *
     * @param cacheMap  The cache map containing key-value pairs.
     * @param key        The key of the entry to read.
     * @param dataSource The data source to fetch entries from if not found in the cache.
     * @return The value associated with the key, or null if not found.
     */
    V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource);
}
