package org.saahil.cache.writingpolicy;

import java.util.Map;

public class WriteBehindPolicy {
    public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {
        public void write(final Map<K, V> cacheMap, final K key, final V value, final DataSource<K, V> dataSource) {
            cacheMap.put(key, value);
            new Thread(new Runnable() {
                public void run() {
                    dataSource.store(key, value);
                }
            }).start();
        }
    }
}
