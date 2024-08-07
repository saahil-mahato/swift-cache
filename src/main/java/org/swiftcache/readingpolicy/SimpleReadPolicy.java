package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;


public class SimpleReadPolicy<K, V> implements IReadingPolicy<K, V> {

    @Override
    public V read(Map<K, V> cacheMap, K key, ICacheRepository<K, V> repository) {
        // This policy only reads from the cache, ignoring the data source.
        return cacheMap.get(key);
    }
}
