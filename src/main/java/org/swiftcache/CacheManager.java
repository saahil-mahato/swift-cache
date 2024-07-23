package org.swiftcache;

import org.swiftcache.cache.Cache;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.writingpolicy.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the initialization and configuration of the cache system.
 * <p>
 * The {@code CacheManager} class is responsible for loading configuration properties from a file,
 * setting up the database connection, and initializing the cache with the appropriate eviction strategy,
 * reading policy, and writing policy.
 * </p>
 * <p>
 * The cache instance is created as a singleton and can be accessed via the {@link #getCache()} method.
 * </p>
 */
public class CacheManager {

    private static Cache<Object, Object> cache;

    static {
        try {
            initializeCache();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize CacheManager", e);
        }
    }

    /**
     * Initializes the cache by loading configuration properties from the {@code config.properties} file,
     * setting up the database connection, and creating the cache with specified policies and strategies.
     * <p>
     * The configuration properties include database URL, user credentials, cache size, eviction strategy,
     * reading policy, and writing policy.
     * </p>
     *
     */
    private static void initializeCache() throws RuntimeException, SQLException, IOException {
        Properties config = new Properties();
        InputStream input = CacheManager.class.getClassLoader().getResourceAsStream("config.properties");
        if (input == null) {
            throw new RuntimeException("Config file not found");
        }
        config.load(input);

        String dbUrl = config.getProperty("db.url");
        String dbUser = config.getProperty("db.user");
        String dbPassword = config.getProperty("db.password");
        long maxSize = Integer.parseInt(config.getProperty("cache.maxSize"));
        String evictionStrategy = config.getProperty("cache.evictionStrategy");
        String readPolicy = config.getProperty("cache.readPolicy");
        String writePolicy = config.getProperty("cache.writePolicy");

        Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        DataSource<Object, Object> dataSource = new DataSource<Object,Object>(connection);

        cache = new Cache<Object,Object>(maxSize, dataSource,
                createEvictionStrategy(evictionStrategy),
                createWritingPolicy(writePolicy),
                createReadingPolicy(readPolicy));
    }

    /**
     * Retrieves the singleton cache instance.
     *
     * @return the {@code Cache<Object, Object>} instance.
     */
    public static Cache<Object, Object> getCache() {
        return cache;
    }

    /**
     * Creates an {@link IEvictionStrategy} based on the provided strategy name.
     *
     * @param strategy the name of the eviction strategy.
     * @return an {@code EvictionStrategy<Object, Object>} implementation.
     * @throws IllegalArgumentException if the strategy name is invalid.
     */
    private static IEvictionStrategy<Object, Object> createEvictionStrategy(String strategy) {
        if ("FIFO".equals(strategy)) {
            return new FIFOEvictionStrategy<Object,Object>();
        } else if ("LRU".equals(strategy)) {
            return new LRUEvictionStrategy<Object,Object>();
        } else {
            throw new IllegalArgumentException("Invalid eviction strategy: " + strategy);
        }
    }

    /**
     * Creates a {@link IReadingPolicy} based on the provided policy name.
     *
     * @param policy the name of the reading policy.
     * @return a {@code ReadingPolicy<Object, Object>} implementation.
     * @throws IllegalArgumentException if the policy name is invalid.
     */
    private static IReadingPolicy<Object, Object> createReadingPolicy(String policy) {
        if ("ReadThrough".equals(policy)) {
            return new ReadThroughPolicy<Object,Object>();
        } else if ("RefreshAhead".equals(policy)) {
            return new RefreshAheadPolicy<Object,Object>(1); // Example refresh interval
        } else if ("Simple".equals(policy)) {
            return new SimpleReadPolicy<Object,Object>();
        } else {
            throw new IllegalArgumentException("Invalid reading policy: " + policy);
        }
    }

    /**
     * Creates a {@link IWritingPolicy} based on the provided policy name.
     *
     * @param policy the name of the writing policy.
     * @return a {@code WritingPolicy<Object, Object>} implementation.
     * @throws IllegalArgumentException if the policy name is invalid.
     */
    private static IWritingPolicy<Object, Object> createWritingPolicy(String policy) {
        if ("WriteAlways".equals(policy)) {
            return new WriteAlwaysPolicy<Object,Object>();
        } else if ("WriteBehind".equals(policy)) {
            return new WriteBehindPolicy<Object,Object>();
        } else if ("WriteIfAbsent".equals(policy)) {
            return new WriteIfAbsentPolicy<Object,Object>();
        } else {
            throw new IllegalArgumentException("Invalid writing policy: " + policy);
        }
    }
}
