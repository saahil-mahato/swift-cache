package org.swiftcache.datasource;

import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.utils.TriFunction;


/**
 * A data source abstraction that delegates operations to an underlying cache repository.
 * This class provides a simple wrapper around an {@link ICacheRepository} to abstract away the specific
 * cache implementation details.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class DataSource<K, V> {

    /**
     * The underlying cache repository.
     */
    private final ICacheRepository<K, V> repository;

    /**
     * Constructs a new DataSource instance with the specified cache repository.
     *
     * @param repository The cache repository to delegate operations to.
     */
    public DataSource(ICacheRepository<K, V> repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a value from the data source based on the given key.
     * This method delegates the operation to the underlying cache repository.
     *
     * @param key The key to retrieve the value for.
     * @return The value associated with the key, or null if not found.
     */
    public V get(K key) {
        return this.repository.get(key);
    }

    /**
     * Stores a key-value pair in the data source.
     * This method delegates the operation to the underlying cache repository.
     *
     * @param key   The key to associate with the value.
     * @param value The value to store in the data source.
     */
    public void put(K key, V value) {
        this.repository.put(key, value);
    }

    /**
     * Removes a key-value pair from the data source.
     * This method delegates the operation to the underlying cache repository.
     *
     * @param key The key of the entry to remove.
     */
    public void remove(K key) {
        this.repository.remove(key);
    }

    /**
     * Executes a custom operation with the cache.
     *
     * @param key       The key to associate with the value.
     * @param value     The value to store in the cache.
     * @param operation A function that defines the operation to be executed.
     * @return The result of the operation.
     */
    public <R> R executeWithCache(K key, V value, TriFunction<K, V, ICacheRepository<K, V>, R> operation) {
        return this.repository.executeWithCache(key, value, operation);
    }
}
