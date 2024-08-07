package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;

/**
 * Interface representing a reading policy for a cache, providing a method
 * to read entries from the cache or a repository.
 *
 * @param <K> the type of keys maintained by this reading policy
 * @param <V> the type of values maintained by this reading policy
 */
public interface IReadingPolicy<K, V> {

    /**
     * Reads the value associated with the specified key from the cache map.
     * If the value is not found in the cache, it retrieves it from the
     * specified repository.
     *
     * @param cacheMap the cache map containing the entries
     * @param key the key whose associated value is to be read
     * @param repository the repository to use for reading the value if not found in the cache
     * @return the value associated with the specified key, or null if not found
     */
    V read(Map<K, V> cacheMap, K key, ICacheRepository<K, V> repository);
}
