package org.saahil.cache.datasource;

public interface IDataSource<K, V> {
    V fetch(K key, String sql);
    void store(K key, V value, String sql);
    void delete(K key, String sql);
}
