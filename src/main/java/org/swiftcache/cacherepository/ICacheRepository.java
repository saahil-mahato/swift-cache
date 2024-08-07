package org.swiftcache.cacherepository;

import org.swiftcache.utils.TriFunction;

/**
 * Interface representing a cache repository that provides methods for
 * basic cache operations such as retrieving, storing, and removing entries.
 *
 * @param <K> the type of keys maintained by this repository
 * @param <V> the type of values maintained by this repository
 */
public interface ICacheRepository<K, V> {

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key, or null if not found
     */
    V get(K key);

    /**
     * Associates the specified value with the specified key in the repository.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    void put(K key, V value);

    /**
     * Removes the entry for the specified key from the repository.
     *
     * @param key the key whose mapping is to be removed from the repository
     */
    void remove(K key);

    /**
     * Executes a specified operation using the cache, allowing for custom logic
     * to be applied with the provided key and value.
     *
     * @param operation the operation to execute with the cache
     * @param key the key to operate on
     * @param value the value to operate with
     * @param <R> the return type of the operation
     * @return the result of the operation
     */
    <R> R executeWithCache(TriFunction<ICacheRepository<K, V>, K, V, R> operation, K key, V value);
}
