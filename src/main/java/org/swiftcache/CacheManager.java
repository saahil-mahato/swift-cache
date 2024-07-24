package org.swiftcache;

import org.swiftcache.cache.Cache;
import org.swiftcache.cache.CacheConfig;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.writingpolicy.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class CacheManager {

    private static Cache<Object, Object> cache;

    private static Connection connection;

    private CacheManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Cache<Object, Object> getCache(CacheConfig config) {
        if (cache == null) {
            initializeCache(config);
        }
        return cache;
    }

    private static void initializeCache(CacheConfig config) {
        try {
            connection = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
            DataSource<Object, Object> dataSource = new DataSource<>(connection);

            cache = new Cache<>(config.getMaxSize(), dataSource,
                    createEvictionStrategy(config.getEvictionStrategy()),
                    createWritingPolicy(config.getWritePolicy()),
                    createReadingPolicy(config.getReadPolicy()));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize cache: database connection error", e);
        }
    }

    public static void createTestTable() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS test_table (testKey VARCHAR(255) PRIMARY KEY, testValue INTEGER);"
        );
    }

    public static void clearTestTable() throws SQLException {
        connection.createStatement().execute(
                "DELETE FROM test_table;"
        );
    }

    public static void deleteTestTable() throws SQLException {
        connection.createStatement().execute(
                "DROP TABLE test_table;"
        );
    }

    private static IEvictionStrategy<Object, Object> createEvictionStrategy(String strategy) {
        if ("FIFO".equals(strategy)) {
            return new FIFOEvictionStrategy<>();
        } else if ("LRU".equals(strategy)) {
            return new LRUEvictionStrategy<>();
        } else {
            throw new IllegalArgumentException("Invalid eviction strategy: " + strategy);
        }
    }

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
