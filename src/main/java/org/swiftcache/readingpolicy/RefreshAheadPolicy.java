package org.swiftcache.readingpolicy;

import org.swiftcache.datasource.IDataSource;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {
    private final Timer timer;
    private final long refreshIntervalMillis;

    public RefreshAheadPolicy(long refreshIntervalMillis) {
        this.refreshIntervalMillis = refreshIntervalMillis;
        this.timer = new Timer("RefreshAheadTimer", true);
    }

    public V readWithDataSource(final Map<K, V> cacheMap, final K key, final IDataSource<K, V> dataSource, final String fetchSql) {
        V value = cacheMap.get(key);
        if (value != null) {
            this.timer.schedule(new TimerTask() {
                public void run() {
                    V freshValue = dataSource.fetch(key, fetchSql);
                    if (freshValue != null) {
                        cacheMap.put(key, freshValue);
                    }
                }
            }, this.refreshIntervalMillis);
        }
        return value;
    }

    public V read(Map<K, V> cacheMap, K key) {
        throw new UnsupportedOperationException("Refresh Ahead policy requires a data source.");
    }
}
