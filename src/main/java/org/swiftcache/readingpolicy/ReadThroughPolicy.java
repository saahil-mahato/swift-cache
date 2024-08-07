package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(ReadThroughPolicy.class.getName());

    @Override
    public V read(Map<K, V> cacheMap, K key, ICacheRepository<K, V> repository) {
        V value = cacheMap.get(key);
        if (value == null) {
            value = repository.get(key);
            if (value != null) {
                cacheMap.put(key, value);
                logger.log(Level.INFO, "Read miss for key: {0}, fetched from data source", key);
            }
        } else {
            logger.log(Level.INFO,"Read hit for key: {0}", key);
        }
        return value;
    }
}
