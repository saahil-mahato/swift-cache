package org.swiftcache;

import org.swiftcache.CacheRepository.ICacheRepository;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.writingpolicy.*;

/**
 * Manages the creation and retrieval of SwiftCache instances.
 * This class simplifies the process of configuring and creating SwiftCache instances
 * by handling the creation of necessary components based on the provided configuration.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class SwiftCacheManager<K, V> {

    private SwiftCache<K, V> swiftCache;

    /**
     * Constructs a new SwiftCacheManager instance.
     * Initializes a SwiftCache instance based on the provided configuration and cache repository.
     *
     * @param config         The configuration parameters for the cache.
     * @param cacheRepository The underlying cache repository.
     */
    public SwiftCacheManager(SwiftCacheConfig config, ICacheRepository<K, V> cacheRepository) {
        initializeCache(config, cacheRepository);
    }

    /**
     * Retrieves the initialized SwiftCache instance.
     *
     * @return The SwiftCache instance.
     */
    public SwiftCache<K, V> getSwiftCache() {
        return this.swiftCache;
    }

    private void initializeCache(SwiftCacheConfig config, ICacheRepository<K, V> cacheRepository) {
        DataSource<K, V> dataSource = new DataSource<>(cacheRepository);

        swiftCache = new SwiftCache<>(config.getMaxSize(), dataSource,
                createEvictionStrategy(config.getEvictionStrategy()),
                createWritingPolicy(config.getWritePolicy()),
                createReadingPolicy(config.getReadPolicy()));
    }

    /**
     * Creates an eviction strategy instance based on the provided strategy name.
     *
     * @param strategy The name of the eviction strategy.
     * @return The corresponding IEvictionStrategy instance.
     * @throws IllegalArgumentException If the strategy is invalid.
     */
    private IEvictionStrategy<K, V> createEvictionStrategy(String strategy) {
        if (SwiftCacheConfig.FIFOEvictionStrategy.equals(strategy)) {
            return new FIFOEvictionStrategy<>();
        } else if (SwiftCacheConfig.LRUEvictionStrategy.equals(strategy)) {
            return new LRUEvictionStrategy<>();
        } else {
            throw new IllegalArgumentException("Invalid eviction strategy: " + strategy);
        }
    }

    /**
     * Creates a reading policy instance based on the provided policy name.
     *
     * @param policy The name of the reading policy.
     * @return The corresponding IReadingPolicy instance.
     * @throws IllegalArgumentException If the policy is invalid.
     */
    private IReadingPolicy<K, V> createReadingPolicy(String policy) {
        if (SwiftCacheConfig.ReadThroughPolicy.equals(policy)) {
            return new ReadThroughPolicy<>();
        } else if (SwiftCacheConfig.RefreshAheadPolicy.equals(policy)) {
            return new RefreshAheadPolicy<>();
        } else if (SwiftCacheConfig.SimpleReadPolicy.equals(policy)) {
            return new SimpleReadPolicy<>();
        } else {
            throw new IllegalArgumentException("Invalid reading policy: " + policy);
        }
    }

    /**
     * Creates a writing policy instance based on the provided policy name.
     *
     * @param policy The name of the writing policy.
     * @return The corresponding IWritingPolicy instance.
     * @throws IllegalArgumentException If the policy is invalid.
     */
    private IWritingPolicy<K, V> createWritingPolicy(String policy) {
        if (SwiftCacheConfig.WriteAlwaysPolicy.equals(policy)) {
            return new WriteAlwaysPolicy<>();
        } else if (SwiftCacheConfig.WriteBehindPolicy.equals(policy)) {
            return new WriteBehindPolicy<>();
        } else if (SwiftCacheConfig.WriteIfAbsentPolicy.equals(policy)) {
            return new WriteIfAbsentPolicy<>();
        } else {
            throw new IllegalArgumentException("Invalid writing policy: " + policy);
        }
    }
}
