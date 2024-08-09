package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the IWritingPolicy interface that uses the Write Always
 * policy. This policy writes the specified value to both the cache and the
 * underlying data source (repository) every time a write operation is performed.
 *
 * @param <K> the type of keys maintained by this writing policy
 * @param <V> the type of values maintained by this writing policy
 */
public class WriteAlwaysPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(WriteAlwaysPolicy.class.getName());

    /**
     * Writes the specified value associated with the specified key to the cache map
     * and the underlying repository. This policy ensures that the value is always
     * written to both locations.
     *
     * @param cacheMap the cache map to write the entry to
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @param repository the repository to write the entry to
     * @return the value that was written
     */
    @Override
    public V write(Map<K, V> cacheMap, K key, V value, ICacheRepository<K, V> repository) {
        cacheMap.put(key, value);
        repository.put(key, value);

        logger.log(Level.INFO, "Written key: {0} to both cache and data source", key);

        return value;
    }
}
