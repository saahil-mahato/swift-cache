package org.swiftcache.cacherepository;

import org.swiftcache.utils.TriFunction;


public interface ICacheRepository<K, V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);

    <R> R executeWithCache(TriFunction<ICacheRepository<K, V>, K, V, R> operation, K key, V value);
}
