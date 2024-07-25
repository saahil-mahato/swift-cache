package org.swiftcache;

import org.swiftcache.CacheRepository.ICacheRepository;
import org.swiftcache.cache.SwiftCache;
import org.swiftcache.cache.SwiftCacheConfig;
import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.*;
import org.swiftcache.readingpolicy.*;
import org.swiftcache.writingpolicy.*;


public class SwiftCacheManager {

    private SwiftCache<Object, Object> swiftCache;

    public SwiftCacheManager(SwiftCacheConfig config, ICacheRepository<Object, Object> cacheRepository) {
       initializeCache(config, cacheRepository);
    }

    private void initializeCache(SwiftCacheConfig config, ICacheRepository<Object, Object> cacheRepository) {
        DataSource<Object, Object> dataSource = new DataSource<>(cacheRepository);

        swiftCache = new SwiftCache<>(config.getMaxSize(), dataSource,
                createEvictionStrategy(config.getEvictionStrategy()),
                createWritingPolicy(config.getWritePolicy()),
                createReadingPolicy(config.getReadPolicy()));
    }

    public SwiftCache<Object, Object> getSwiftCache() {
        return this.swiftCache;
    }

    private static IEvictionStrategy<Object, Object> createEvictionStrategy(String strategy) {
        if (SwiftCacheConfig.FIFOEvictionStrategy.equals(strategy)) {
            return new FIFOEvictionStrategy<>();
        } else if (SwiftCacheConfig.LRUEvictionStrategy.equals(strategy)) {
            return new LRUEvictionStrategy<>();
        } else {
            throw new IllegalArgumentException("Invalid eviction strategy: " + strategy);
        }
    }

    private static IReadingPolicy<Object, Object> createReadingPolicy(String policy) {
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

    private static IWritingPolicy<Object, Object> createWritingPolicy(String policy) {
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
