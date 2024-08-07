package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WriteIfAbsentPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(WriteIfAbsentPolicy.class.getName());

    @Override
    public V write(Map<K, V> cacheMap, K key, V value, ICacheRepository<K, V> repository) {
        V newValue;
        if (!cacheMap.containsKey(key)) {
            // Key not present in cache, so write to both cache and data source
            cacheMap.put(key, value);
            repository.put(key, value);
            newValue = value;
            logger.log(Level.INFO, "Written key: {0} to cache and data source (Write-If-Absent)", key);
        } else {
            newValue = cacheMap.get(key);
            logger.log(Level.INFO, "Key: {0} already exists in cache (Write-If-Absent)", key);
        }

        return newValue;
    }
}
