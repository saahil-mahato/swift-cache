package org.swiftcache.datasource;

/**
 * Interface for a data source that provides methods for fetching, storing, and deleting data.
 * <p>
 * This interface defines the operations required for interacting with a data source to manage key-value pairs.
 * Implementations of this interface will typically use a specific database or storage system to perform these operations.
 * </p>
 *
 * @param <K> The type of keys used for data operations.
 * @param <V> The type of values used for data operations.
 */
public interface IDataSource<K, V> {

    /**
     * Fetches a value associated with the specified key from the data source.
     * <p>
     * This method executes a query using the provided SQL statement to retrieve the value for the given key.
     * </p>
     *
     * @param key The key whose associated value is to be fetched.
     * @param sql The SQL query to be executed, which should include a parameter for the key.
     * @return The value associated with the specified key, or null if no value is found or an error occurs.
     */
    V fetch(K key, String sql);

    /**
     * Stores a key-value pair in the data source.
     * <p>
     * This method executes an update statement using the provided SQL to store the given key and value in the data source.
     * </p>
     *
     * @param key The key to be stored.
     * @param value The value to be associated with the key.
     * @param sql The SQL update statement to be executed, which should include parameters for the key and value.
     */
    void store(K key, V value, String sql);

    /**
     * Deletes the entry associated with the specified key from the data source.
     * <p>
     * This method executes a SQL update statement to remove the entry for the given key from the data source.
     * </p>
     *
     * @param key The key whose associated entry is to be deleted.
     * @param sql The SQL update statement to be executed, which should include a parameter for the key.
     */
    void delete(K key, String sql);
}
