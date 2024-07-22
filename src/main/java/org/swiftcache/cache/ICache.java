package org.swiftcache.cache;

/**
 * Represents a generic cache interface with basic cache operations.
 * <p>
 * This interface defines the essential operations for interacting with a cache, including adding, retrieving, removing entries,
 * and querying the cache size.
 * </p>
 *
 * @param <K> The type of keys used in the cache.
 * @param <V> The type of values stored in the cache.
 */
public interface ICache<K, V> {

    /**
     * Adds an entry to the cache with the specified key and value.
     * <p>
     * If the cache is at its maximum capacity, an entry may be evicted based on the cache's eviction policy.
     * </p>
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @param storeSql The SQL statement used to store the entry in a data source.
     */
    void put(K key, V value, String storeSql);

    /**
     * Retrieves the value associated with the specified key from the cache.
     * <p>
     * If the value is not found in the cache and the fetchSql is provided, it may be fetched from a data source.
     * </p>
     *
     * @param key The key whose associated value is to be returned.
     * @param fetchSql The SQL statement used to fetch the entry from the data source if not present in the cache.
     * @return The value associated with the specified key, or null if no value is found.
     */
    V get(K key, String fetchSql);

    /**
     * Removes the entry associated with the specified key from the cache and the data source.
     * <p>
     * The entry is removed from the cache and also deleted from the data source using the provided SQL statement.
     * </p>
     *
     * @param key The key of the entry to be removed.
     * @param deleteSql The SQL statement used to delete the entry from the data source.
     */
    void remove(K key, String deleteSql);

    /**
     * Returns the number of entries currently in the cache.
     *
     * @return The number of entries in the cache.
     */
    long size();

    /**
     * Clears all entries from the cache.
     * <p>
     * This method removes all entries from the cache, making it empty.
     * </p>
     */
    void clear();
}
