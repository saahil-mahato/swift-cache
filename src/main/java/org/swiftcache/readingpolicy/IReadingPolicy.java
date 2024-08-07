package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;


public interface IReadingPolicy<K, V> {

    V read(Map<K, V> cacheMap, K key, ICacheRepository<K, V> repository);
}
