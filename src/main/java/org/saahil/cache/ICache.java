package org.saahil.cache;

public interface ICache<K,V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    long size();
    void clear();
}

