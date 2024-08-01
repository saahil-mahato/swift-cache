package org.swiftcache.cacherepository;

/**
 * Interface defining the basic operations for a cache repository.
 * This interface provides a common contract for cache implementations,
 * allowing for different cache strategies to be interchangeable.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public interface ICacheRepository<K, V> {

    /**
     * Retrieves a value from the cache based on the given key.
     *
     * @param key The key to retrieve the value for.
     * @return The value associated with the key, or null if not found.
     */
    V get(K key);

    /**
     * Stores a key-value pair in the cache.
     *
     * @param key   The key to associate with the value.
     * @param value The value to store in the cache.
     */
    void put(K key, V value);

    /**
     * Removes a key-value pair from the cache.
     *
     * @param key The key of the entry to remove.
     */
    void remove(K key);
}
