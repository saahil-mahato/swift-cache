package org.swiftcache;

import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.writingpolicy.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Manages the creation and configuration of a SwiftCache instance. This class
 * initializes the cache with specified eviction, reading, and writing policies
 * based on the provided configuration.
 *
 * @param <K> the type of keys maintained by the cache
 * @param <V> the type of values maintained by the cache
 */
public class SwiftCacheManager<K, V> {

    private SwiftCache<K, V> swiftCache;

    /**
     * Constructs a SwiftCacheManager with the specified configuration.
     *
     * @param config the configuration for the cache
     */
    public SwiftCacheManager(SwiftCacheConfig config) {
        initializeCache(config);
    }

    /**
     * Returns the SwiftCache instance managed by this manager.
     *
     * @return the SwiftCache instance
     */
    public SwiftCache<K, V> getSwiftCache() {
        return this.swiftCache;
    }

    /**
     * Initializes the SwiftCache with the specified configuration.
     *
     * @param config the configuration for the cache
     */
    private void initializeCache(SwiftCacheConfig config) {
        swiftCache = new SwiftCache<>(config.getMaxSize(),
                createEvictionStrategy(config.getEvictionStrategy()),
                createWritingPolicy(config.getWritePolicy()),
                createReadingPolicy(config.getReadPolicy()));
    }

    /**
     * Creates an eviction strategy based on the specified strategy name.
     *
     * @param strategy the name of the eviction strategy
     * @return the corresponding eviction strategy
     */
    private IEvictionStrategy<K, V> createEvictionStrategy(String strategy) {
        Map<String, Supplier<IEvictionStrategy<K, V>>> evictionStrategies = new HashMap<>();
        evictionStrategies.put(SwiftCacheConfig.FIFO_EVICTION_STRATEGY, FIFOEvictionStrategy::new);
        evictionStrategies.put(SwiftCacheConfig.LRU_EVICTION_STRATEGY, LRUEvictionStrategy::new);

        return createStrategy(evictionStrategies, strategy, "eviction strategy");
    }

    /**
     * Creates a reading policy based on the specified policy name.
     *
     * @param policy the name of the reading policy
     * @return the corresponding reading policy
     */
    private IReadingPolicy<K, V> createReadingPolicy(String policy) {
        Map<String, Supplier<IReadingPolicy<K, V>>> readingPolicies = new HashMap<>();
        readingPolicies.put(SwiftCacheConfig.READ_THROUGH_POLICY, ReadThroughPolicy::new);
        readingPolicies.put(SwiftCacheConfig.REFRESH_AHEAD_POLICY, RefreshAheadPolicy::new);
        readingPolicies.put(SwiftCacheConfig.SIMPLE_READ_POLICY, SimpleReadPolicy::new);

        return createStrategy(readingPolicies, policy, "reading policy");
    }

    /**
     * Creates a writing policy based on the specified policy name.
     *
     * @param policy the name of the writing policy
     * @return the corresponding writing policy
     */
    private IWritingPolicy<K, V> createWritingPolicy(String policy) {
        Map<String, Supplier<IWritingPolicy<K, V>>> writingPolicies = new HashMap<>();
        writingPolicies.put(SwiftCacheConfig.WRITE_ALWAYS_POLICY, WriteAlwaysPolicy::new);
        writingPolicies.put(SwiftCacheConfig.WRITE_BEHIND_POLICY, WriteBehindPolicy::new);
        writingPolicies.put(SwiftCacheConfig.WRITE_IF_ABSENT_POLICY, WriteIfAbsentPolicy::new);

        return createStrategy(writingPolicies, policy, "writing policy");
    }

    /**
     * Creates a strategy based on the specified key and type. Throws an exception
     * if the key is not valid.
     *
     * @param strategies a map of strategy names to their corresponding suppliers
     * @param key the name of the strategy to create
     * @param type the type of strategy (for error messaging)
     * @param <T> the type of the strategy
     * @return the created strategy
     * @throws IllegalArgumentException if the key is invalid
     */
    private <T> T createStrategy(Map<String, Supplier<T>> strategies, String key, String type) {
        Supplier<T> strategy = strategies.get(key);
        if (strategy != null) {
            return strategy.get();
        } else {
            throw new IllegalArgumentException("Invalid " + type + ": " + key);
        }
    }
}
