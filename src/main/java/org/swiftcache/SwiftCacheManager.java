package org.swiftcache;

import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.writingpolicy.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the initialization and configuration of the cache system.
 * Provides methods for creating and configuring a cache instance and managing database tables for testing purposes.
 */
public class SwiftCacheManager {

    private static SwiftCache<Object, Object> swiftCache;
    private static Connection connection;

    // Private constructor to prevent instantiation
    private SwiftCacheManager() {
        // Prevent instantiation
    }

    /**
     * Retrieves the singleton cache instance, initializing it if necessary.
     *
     * @param config the configuration settings for initializing the cache
     * @return the singleton cache instance
     * @throws RuntimeException if initialization fails
     */
    public static synchronized SwiftCache<Object, Object> getCache(SwiftCacheConfig config) {
        if (swiftCache == null) {
            initializeCache(config);
        }
        return swiftCache;
    }

    /**
     * Initializes the cache and sets up the database connection using the provided configuration.
     *
     * @param config the configuration settings for initializing the cache
     * @throws RuntimeException if database connection fails
     */
    private static void initializeCache(SwiftCacheConfig config) {
        try {
            connection = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
            DataSource<Object, Object> dataSource = new DataSource<>(connection);

            swiftCache = new SwiftCache<>(config.getMaxSize(), dataSource,
                    createEvictionStrategy(config.getEvictionStrategy()),
                    createWritingPolicy(config.getWritePolicy()),
                    createReadingPolicy(config.getReadPolicy()));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize cache: database connection error", e);
        }
    }

    /**
     * Creates the test table in the database if it does not already exist.
     *
     * @throws SQLException if a database access error occurs
     */
    public static void createTestTable() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS test_table (testKey VARCHAR(255) PRIMARY KEY, testValue INTEGER);"
        );
    }

    /**
     * Clears all data from the test table.
     *
     * @throws SQLException if a database access error occurs
     */
    public static void clearTestTable() throws SQLException {
        connection.createStatement().execute(
                "DELETE FROM test_table;"
        );
    }

    /**
     * Deletes the test table from the database.
     *
     * @throws SQLException if a database access error occurs
     */
    public static void deleteTestTable() throws SQLException {
        connection.createStatement().execute(
                "DROP TABLE test_table;"
        );
    }

    /**
     * Creates an eviction strategy based on the specified strategy type.
     *
     * @param strategy the type of eviction strategy (e.g., "FIFO", "LRU")
     * @return an instance of the specified eviction strategy
     * @throws IllegalArgumentException if the strategy type is invalid
     */
    private static IEvictionStrategy<Object, Object> createEvictionStrategy(String strategy) {
        if ("FIFO".equals(strategy)) {
            return new FIFOEvictionStrategy<>();
        } else if ("LRU".equals(strategy)) {
            return new LRUEvictionStrategy<>();
        } else {
            throw new IllegalArgumentException("Invalid eviction strategy: " + strategy);
        }
    }

    /**
     * Creates a reading policy based on the specified policy type.
     *
     * @param policy the type of reading policy (e.g., "ReadThrough", "RefreshAhead", "Simple")
     * @return an instance of the specified reading policy
     * @throws IllegalArgumentException if the policy type is invalid
     */
    private static IReadingPolicy<Object, Object> createReadingPolicy(String policy) {
        if ("ReadThrough".equals(policy)) {
            return new ReadThroughPolicy<>();
        } else if ("RefreshAhead".equals(policy)) {
            return new RefreshAheadPolicy<>(1); // Example refresh interval
        } else if ("Simple".equals(policy)) {
            return new SimpleReadPolicy<>();
        } else {
            throw new IllegalArgumentException("Invalid reading policy: " + policy);
        }
    }

    /**
     * Creates a writing policy based on the specified policy type.
     *
     * @param policy the type of writing policy (e.g., "WriteAlways", "WriteBehind", "WriteIfAbsent")
     * @return an instance of the specified writing policy
     * @throws IllegalArgumentException if the policy type is invalid
     */
    private static IWritingPolicy<Object, Object> createWritingPolicy(String policy) {
        if ("WriteAlways".equals(policy)) {
            return new WriteAlwaysPolicy<>();
        } else if ("WriteBehind".equals(policy)) {
            return new WriteBehindPolicy<>();
        } else if ("WriteIfAbsent".equals(policy)) {
            return new WriteIfAbsentPolicy<>();
        } else {
            throw new IllegalArgumentException("Invalid writing policy: " + policy);
        }
    }
}
