package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WriteAlwaysPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(WriteAlwaysPolicy.class.getName());

    @Override
    public V write(Map<K, V> cacheMap, K key, V value, ICacheRepository<K, V> repository) {
        // Update the cache first
        cacheMap.put(key, value);

        // Update the data source asynchronously (or synchronously depending on implementation)
        repository.put(key, value);
        logger.log(Level.INFO, "Written key: {0} to both cache and data source", key);

        return value;
    }
}
