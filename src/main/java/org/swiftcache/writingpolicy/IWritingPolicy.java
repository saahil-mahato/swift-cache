package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;


public interface IWritingPolicy<K, V> {

    V write(Map<K, V> cacheMap, K key, V value, ICacheRepository<K, V> repository);
}
