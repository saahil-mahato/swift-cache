package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the IReadingPolicy interface that uses the Read-Through
 * policy. This policy attempts to read a value from the cache first, and if
 * the value is not present, it retrieves it from the underlying data source
 * (repository) and updates the cache.
 *
 * @param <K> the type of keys maintained by this reading policy
 * @param <V> the type of values maintained by this reading policy
 */
public class ReadThroughPolicy<K, V> implements IReadingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(ReadThroughPolicy.class.getName());

    /**
     * Reads the value associated with the specified key. If the value is not
     * found in the cache, it retrieves it from the specified repository and
     * updates the cache.
     *
     * @param cacheMap the cache map containing the entries
     * @param key the key whose associated value is to be read
     * @param repository the repository to use for reading the value if not found in the cache
     * @return the value associated with the specified key, or null if not found
     */
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
