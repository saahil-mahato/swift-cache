package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;

/**
 * An implementation of the IReadingPolicy interface that uses the Simple Read
 * policy. This policy only reads values from the cache and ignores the underlying
 * data source (repository).
 *
 * @param <K> the type of keys maintained by this reading policy
 * @param <V> the type of values maintained by this reading policy
 */
public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {

    /**
     * Reads the value associated with the specified key from the cache.
     * This policy does not attempt to fetch the value from the repository.
     *
     * @param cacheMap the cache map containing the entries
     * @param key the key whose associated value is to be read
     * @param repository the repository (ignored by this policy)
     * @return the value associated with the specified key, or null if not found in the cache
     */
    @Override
    public V read(Map<K, V> cacheMap, K key, ICacheRepository<K, V> repository) {
        // This policy only reads from the cache, ignoring the data source.
        return cacheMap.get(key);
    }
}
