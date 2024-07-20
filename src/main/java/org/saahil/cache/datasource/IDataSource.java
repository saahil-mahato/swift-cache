package org.saahil.cache.datasource;

public interface IDataSource<K, V> {
    V fetch(K key);
    void store(K key, V value);
    void delete(K key);
}
