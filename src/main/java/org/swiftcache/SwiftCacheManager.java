package org.swiftcache;

import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.writingpolicy.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class SwiftCacheManager<K, V> {

    private SwiftCache<K, V> swiftCache;

    public SwiftCacheManager(SwiftCacheConfig config) {
        initializeCache(config);
    }

    public SwiftCache<K, V> getSwiftCache() {
        return this.swiftCache;
    }

    private void initializeCache(SwiftCacheConfig config) {

        swiftCache = new SwiftCache<>(config.getMaxSize(),
                createEvictionStrategy(config.getEvictionStrategy()),
                createWritingPolicy(config.getWritePolicy()),
                createReadingPolicy(config.getReadPolicy()));
    }

    private IEvictionStrategy<K, V> createEvictionStrategy(String strategy) {
        Map<String, Supplier<IEvictionStrategy<K, V>>> evictionStrategies = new HashMap<>();
        evictionStrategies.put(SwiftCacheConfig.FIFO_EVICTION_STRATEGY, FIFOEvictionStrategy::new);
        evictionStrategies.put(SwiftCacheConfig.LRU_EVICTION_STRATEGY, LRUEvictionStrategy::new);

        return createStrategy(evictionStrategies, strategy, "eviction strategy");
    }

    private IReadingPolicy<K, V> createReadingPolicy(String policy) {
        Map<String, Supplier<IReadingPolicy<K, V>>> readingPolicies = new HashMap<>();
        readingPolicies.put(SwiftCacheConfig.READ_THROUGH_POLICY, ReadThroughPolicy::new);
        readingPolicies.put(SwiftCacheConfig.REFRESH_AHEAD_POLICY, RefreshAheadPolicy::new);
        readingPolicies.put(SwiftCacheConfig.SIMPLE_READ_POLICY, SimpleReadPolicy::new);

        return createStrategy(readingPolicies, policy, "reading policy");
    }

    private IWritingPolicy<K, V> createWritingPolicy(String policy) {
        Map<String, Supplier<IWritingPolicy<K, V>>> writingPolicies = new HashMap<>();
        writingPolicies.put(SwiftCacheConfig.WRITE_ALWAYS_POLICY, WriteAlwaysPolicy::new);
        writingPolicies.put(SwiftCacheConfig.WRITE_BEHIND_POLICY, WriteBehindPolicy::new);
        writingPolicies.put(SwiftCacheConfig.WRITE_IF_ABSENT_POLICY, WriteIfAbsentPolicy::new);

        return createStrategy(writingPolicies, policy, "writing policy");
    }

    private <T> T createStrategy(Map<String, Supplier<T>> strategies, String key, String type) {
        Supplier<T> strategy = strategies.get(key);
        if (strategy != null) {
            return strategy.get();
        } else {
            throw new IllegalArgumentException("Invalid " + type + ": " + key);
        }
    }
}
