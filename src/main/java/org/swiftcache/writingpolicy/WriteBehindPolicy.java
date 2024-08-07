package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteBehindPolicy.class.getName());

    @Override
    public V write(Map<K, V> cacheMap, final K key, final V value, final ICacheRepository<K, V> repository) {
        // Update the cache first
        cacheMap.put(key, value);

        // Update the data source asynchronously in a background thread
        new Thread(() -> {
            repository.put(key, value);
            LOGGER.log(Level.INFO, "Written key: {0} to cache, queued for background write to data source", key);
        }).start();

        return value;
    }
}
