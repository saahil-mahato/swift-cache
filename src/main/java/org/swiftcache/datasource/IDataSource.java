package org.swiftcache.datasource;

public interface IDataSource<K, V> {
    V fetch(K key, String sql);
    void store(String key, int value, String sql);
    void delete(K key, String sql);
}
