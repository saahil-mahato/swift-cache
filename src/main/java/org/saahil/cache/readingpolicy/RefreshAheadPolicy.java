package org.saahil.cache.readingpolicy;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {
    private final Timer timer;
    private final long refreshIntervalMillis;

    public RefreshAheadPolicy(long refreshIntervalMillis) {
        this.refreshIntervalMillis = refreshIntervalMillis;
        this.timer = new Timer(true);
    }

    public V read(final Map<K, V> cacheMap, final K key, final DataSource<K, V> dataSource) {
        V value = cacheMap.get(key);
        if (value != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    V freshValue = dataSource.fetch(key);
                    if (freshValue != null) {
                        cacheMap.put(key, freshValue);
                    }
                }
            }, refreshIntervalMillis);
        }
        return value;
    }
}
