package org.swiftcache.cache;

public interface ICache<K,V> {
    void put(K key, V value, String storeSql);
    V get(K key, String fetchSql);
    void remove(K key, String deleteSql);
    long size();
    void clear();
}

