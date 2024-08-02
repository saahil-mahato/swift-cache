package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Refresh-Ahead reading policy for a cache.
 * In Refresh-Ahead policy, when a key is found in the cache, a background thread
 * is launched to fetch a fresh value from the data source. The current cached value
 * is still returned. This ensures that the data in the cache is eventually consistent
 * with the data source, even if it might be slightly stale at times.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(RefreshAheadPolicy.class.getName());

    @Override
    public V read(final Map<K, V> cacheMap, final K key, final DataSource<K, V> dataSource) {
        V value = cacheMap.get(key);

        // Start a background thread to refresh the value asynchronously
        new Thread(() -> {
            V freshValue = dataSource.get(key);
            if (freshValue != null) {
                cacheMap.put(key, freshValue);
                logger.log(Level.INFO, "Value for key: {0} refreshed in background", key);
            }
        }).start();

        return value;
    }
}
