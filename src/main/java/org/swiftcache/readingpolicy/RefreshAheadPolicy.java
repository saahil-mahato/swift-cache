package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(RefreshAheadPolicy.class.getName());

    @Override
    public V read(final Map<K, V> cacheMap, final K key, final ICacheRepository<K, V> repository) {
        V value = cacheMap.get(key);

        // Start a background thread to refresh the value asynchronously
        new Thread(() -> {
            V freshValue = repository.get(key);
            cacheMap.put(key, freshValue);
            logger.log(Level.INFO, "Value for key: {0} refreshed in background", key);
        }).start();

        return value;
    }
}
