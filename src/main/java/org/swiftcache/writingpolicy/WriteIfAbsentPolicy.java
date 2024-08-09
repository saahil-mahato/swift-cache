package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the IWritingPolicy interface that uses the Write-If-Absent
 * policy. This policy writes the specified value to both the cache and the underlying
 * data source (repository) only if the key is not already present in the cache.
 *
 * @param <K> the type of keys maintained by this writing policy
 * @param <V> the type of values maintained by this writing policy
 */
public class WriteIfAbsentPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(WriteIfAbsentPolicy.class.getName());

    /**
     * Writes the specified value associated with the specified key to the cache map
     * and the underlying repository if the key is not already present in the cache.
     *
     * @param cacheMap the cache map to write the entry to
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @param repository the repository to write the entry to
     * @return the value that was written or the existing value if the key was already present
     */
    @Override
    public V write(Map<K, V> cacheMap, K key, V value, ICacheRepository<K, V> repository) {

        if (cacheMap.containsKey(key)) {
            logger.log(Level.INFO, "Key: {0} already exists in cache (Write-If-Absent)", key);

            return cacheMap.get(key);
        }

        // Key not present in cache, so write to both cache and data source
        cacheMap.put(key, value);
        repository.put(key, value);

        logger.log(Level.INFO, "Written key: {0} to cache and data source (Write-If-Absent)", key);

        return value;
    }
}
