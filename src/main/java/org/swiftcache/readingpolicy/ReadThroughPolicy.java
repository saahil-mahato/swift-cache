package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;
import java.util.logging.Logger;

/**
 * This class implements the Read-Through reading policy for a cache.
 * In Read-Through policy, when a key is not found in the cache, the data source is
 * always consulted to retrieve the value. If found in the data source, the value is
 * stored in the cache before being returned.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(ReadThroughPolicy.class.getName());

    /**
     * Reads a value from the cache or data source using the Read-Through policy.
     * This method first checks the cache for the given key. If the key is not found,
     * it retrieves the value from the data source and stores it in the cache before returning it.
     *
     * @param cacheMap  The cache map containing key-value pairs.
     * @param key        The key of the entry to read.
     * @param dataSource The data source to fetch entries from if not found in the cache.
     * @return The value associated with the key, or null if not found in the cache or data source.
     */
    @Override
    public V read(Map<K, V> cacheMap, K key, DataSource<K, V> dataSource) {
        V value = cacheMap.get(key);
        if (value == null) {
            value = dataSource.get(key);
            if (value != null) {
                cacheMap.put(key, value);
                LOGGER.info("Read miss for key: " + key + ", fetched from data source");
            }
        } else {
            LOGGER.info("Read hit for key: " + key);
        }
        return value;
    }
}
