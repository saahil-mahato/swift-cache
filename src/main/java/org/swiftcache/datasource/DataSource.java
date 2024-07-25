package org.swiftcache.datasource;

import org.swiftcache.CacheRepository.ICacheRepository;

public class DataSource<K, V> {
    private final ICacheRepository<K, V> repository;

    public DataSource(ICacheRepository<K, V> repository) {
        this.repository = repository;
    }

    public V get(K key) {
        return this.repository.get(key);
    }

    public void put(K key, V value) {
        this.repository.put(key, value);
    }

    public void remove(K key) {
        this.repository.remove(key);
    }
}