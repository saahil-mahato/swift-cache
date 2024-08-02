package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Write-Always writing policy for a cache.
 * In Write-Always policy, all writes are immediately persisted to both the cache
 * and the data source. This policy ensures strong data consistency, but it can also
 * lead to performance overhead due to the extra write operation to the data source.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class WriteAlwaysPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(WriteAlwaysPolicy.class.getName());

    @Override
    public V write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource) {
        // Update the cache first
        cacheMap.put(key, value);

        // Update the data source asynchronously (or synchronously depending on implementation)
        dataSource.put(key, value);
        logger.log(Level.INFO, "Written key: {0} to both cache and data source", key);

        return value;
    }
}
