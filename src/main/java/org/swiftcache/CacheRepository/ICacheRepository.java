package org.swiftcache.CacheRepository;

public interface ICacheRepository<K,V> {
    V get(K key);
    void put(K key, V value);
    void remove(K key);
}