package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements the Refresh-Ahead policy for cache management.
 * <p>
 * The Refresh-Ahead policy maintains cache freshness by scheduling automatic refreshes for cache entries.
 * When a cache entry is accessed, it triggers an asynchronous task to refresh the entry from the data source
 * after a specified interval. This ensures that the cache entry is updated with fresh data before it is
 * requested again, thus reducing cache misses.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {
    private final Timer timer;
    private final long refreshIntervalMillis;

    /**
     * Constructs a Refresh-Ahead policy with the specified refresh interval.
     * <p>
     * The refresh interval determines how long to wait before automatically refreshing a cache entry
     * from the data source after it has been accessed. The interval is specified in milliseconds.
     * </p>
     *
     * @param refreshIntervalMillis The time in milliseconds to wait before refreshing a cache entry.
     */
    public RefreshAheadPolicy(long refreshIntervalMillis) {
        this.refreshIntervalMillis = refreshIntervalMillis;
        this.timer = new Timer("RefreshAheadTimer", true);
    }

    /**
     * Reads a value associated with the specified key from the cache and schedules a refresh for the cache entry.
     * <p>
     * If the value is present in the cache, this method schedules an asynchronous task to refresh the value from
     * the external data source after the specified refresh interval. The refreshed value will replace the old value
     * in the cache if the new value is not null.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read and refreshed.
     * @param dataSource The data source used to fetch the value if a refresh is required.
     * @param fetchSql The SQL query to be executed to fetch the value from the data source.
     * @return The value associated with the specified key, either from the cache or the result of a refresh operation.
     */
    public V readWithDataSource(final Map<K, V> cacheMap, final K key, final IDataSource<K, V> dataSource, final String fetchSql) {
        V value = cacheMap.get(key);
        if (value != null) {
            this.timer.schedule(new TimerTask() {
                public void run() {
                    V freshValue = dataSource.fetch(key, fetchSql);
                    if (freshValue != null) {
                        cacheMap.remove(key);
                        cacheMap.put(key, freshValue);
                    }
                }
            }, this.refreshIntervalMillis);
        }
        return value;
    }

    /**
     * Reads a value associated with the specified key from the cache.
     * <p>
     * This method is not supported in the Refresh-Ahead policy as it requires a data source to schedule refreshes.
     * Attempting to call this method will result in an {@link UnsupportedOperationException}.
     * </p>
     *
     * @param cacheMap The map containing the cache entries.
     * @param key The key whose associated value is to be read from the cache.
     * @return This method will always throw an {@link UnsupportedOperationException}.
     * @throws UnsupportedOperationException Always thrown to indicate that the method is not supported.
     */
    public V read(Map<K, V> cacheMap, K key) {
        throw new UnsupportedOperationException("Refresh Ahead policy requires a data source.");
    }
}
