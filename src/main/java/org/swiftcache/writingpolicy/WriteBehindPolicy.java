package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Write-Behind writing policy for a cache.
 * In Write-Behind policy, writes are first updated in the cache, and then asynchronously
 * written to the data source in a background thread. This policy improves write performance
 * by reducing the number of synchronous data source interactions, but it introduces a window
 * of data inconsistency between the cache and the data source until the background write completes.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteBehindPolicy.class.getName());

    @Override
    public V write(Map<K, V> cacheMap, final K key, final V value, final DataSource<K, V> dataSource) {
        // Update the cache first
        cacheMap.put(key, value);

        // Update the data source asynchronously in a background thread
        new Thread(() -> {
            dataSource.put(key, value);
            LOGGER.log(Level.INFO, "Written key: {0} to cache, queued for background write to data source", key);
        }).start();

        return value;
    }
}
