package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;

/**
 * Interface defining the strategy for writing entries to a cache.
 * This interface allows for different policies to be implemented for writing data,
 * providing flexibility in how the cache interacts with the underlying data source.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public interface IWritingPolicy<K, V> {

    /**
     * Writes an entry to the cache and potentially the data source based on the policy.
     * This method defines how the writing policy handles storing a key-value pair.
     * The implementation might update the cache immediately and potentially write
     * the data to the underlying data source as well.
     *
     * @param cacheMap  The cache map containing key-value pairs.
     * @param key        The key of the entry to write.
     * @param value      The value to associate with the key.
     * @param dataSource The data source to write entries to (may vary depending on policy).
     */
    V write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource);
}
