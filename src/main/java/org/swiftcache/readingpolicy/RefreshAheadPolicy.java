package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation of the Refresh-Ahead reading policy for caching.
 * This policy fetches a value from the cache if present; if it is present, it schedules an asynchronous
 * refresh of the cache entry using the data source, ensuring that the cache is updated before the value
 * becomes stale.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {
    private final Timer timer;
    private final long refreshIntervalMillis;

    /**
     * Constructs a new RefreshAheadPolicy with the specified refresh interval.
     *
     * @param refreshIntervalMillis the interval in milliseconds after which the cache entry will be refreshed
     */
    public RefreshAheadPolicy(long refreshIntervalMillis) {
        this.refreshIntervalMillis = refreshIntervalMillis;
        this.timer = new Timer("RefreshAheadTimer", true);
    }

    /**
     * Reads a value from the cache based on the provided key. If the key is present in the cache,
     * schedules an asynchronous task to refresh the value from the data source after the specified interval.
     * The cache is updated with the new value if it is fetched successfully.
     *
     * @param cacheMap   the current map of cache entries
     * @param key        the key whose associated value is to be read
     * @param dataSource the data source to be used to fetch a fresh value if the key is present in the cache
     * @param fetchSql   the SQL query to retrieve the value from the data source
     * @return the value associated with the specified key, or {@code null} if the key is not present in the cache
     */
    @Override
    public V readWithDataSource(final Map<K, V> cacheMap, final K key, final DataSource<K, V> dataSource, final String fetchSql) {
        V value = cacheMap.get(key);
        if (value != null) {
            this.timer.schedule(new TimerTask() {
                @Override
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
     * This method is not supported by the Refresh-Ahead policy and throws an exception.
     * The Refresh-Ahead policy requires the use of {@link #readWithDataSource(Map, Object, DataSource, String)}
     * to handle reads.
     *
     * @param cacheMap the current map of cache entries
     * @param key      the key whose associated value is to be read
     * @return this method always throws {@link UnsupportedOperationException}
     * @throws UnsupportedOperationException if this method is called
     */
    @Override
    public V read(Map<K, V> cacheMap, K key) {
        throw new UnsupportedOperationException("Refresh Ahead policy requires a data source.");
    }
}
