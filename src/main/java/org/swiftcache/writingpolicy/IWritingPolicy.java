package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;

/**
 * Interface representing a writing policy for a cache, providing a method
 * to write entries to the cache and the underlying data source (repository).
 *
 * @param <K> the type of keys maintained by this writing policy
 * @param <V> the type of values maintained by this writing policy
 */
public interface IWritingPolicy<K, V> {

    /**
     * Writes the specified value associated with the specified key to the cache map
     * and the underlying repository.
     *
     * @param cacheMap the cache map to write the entry to
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @param repository the repository to write the entry to
     * @return the previous value associated with the key, or null if there was no mapping for the key
     */
    V write(Map<K, V> cacheMap, K key, V value, ICacheRepository<K, V> repository);
}
