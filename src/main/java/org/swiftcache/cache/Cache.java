package org.swiftcache.cache;

import org.swiftcache.datasource.IDataSource;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache<K, V> implements ICache<K, V> {
    private final long maxSize;
    private final Map<K, V> cacheMap;
    private final Queue<K> evictionQueue;
    private final ReadWriteLock lock;
    private final IDataSource<K, V> dataSource;
    private final IEvictionStrategy<K, V> evictionStrategy;
    private final IWritingPolicy<K,V> writingPolicy;
    private final IReadingPolicy<K, V> readingPolicy;

    public Cache(long maxSize,
                 IDataSource<K, V> dataSource,
                 IEvictionStrategy<K, V> evictionStrategy,
                 IWritingPolicy<K, V> writingPolicy,
                 IReadingPolicy<K, V> readingPolicy) {
        this.maxSize = maxSize;
        this.cacheMap = new LinkedHashMap<K, V>((int)maxSize, 0.75f, true);
        this.evictionQueue = new LinkedList<K>();
        this.lock = new ReentrantReadWriteLock();
        this.dataSource = dataSource;
        this.evictionStrategy = evictionStrategy;
        this.writingPolicy = writingPolicy;
        this.readingPolicy = readingPolicy;
    }

    public void put(K key, V value, String storeSql) {
        lock.writeLock().lock();
        storeSql = storeSql.trim();
        try {
            if (cacheMap.size() >= maxSize) {
                K evictedKey = evictionStrategy.evict(cacheMap, evictionQueue);
                if (evictedKey != null) {
                    cacheMap.remove(evictedKey);
                    evictionQueue.remove(evictedKey);
                }
            }
            writingPolicy.write(cacheMap, key, value, dataSource, storeSql);
            evictionQueue.remove(key);
            evictionQueue.offer(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public V get(K key, String fetchSql) {
        lock.readLock().lock();
        fetchSql = fetchSql.trim();
        try {
            V value;
            if (fetchSql.length() == 0) {
                value = readingPolicy.read(cacheMap, key);
            } else {
                value = readingPolicy.readWithDataSource(cacheMap, key, dataSource, fetchSql);
            }
            if (value != null) {
                evictionQueue.remove(key);
                evictionQueue.offer(key);
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void remove(K key, String deleteSql) {
        lock.writeLock().lock();
        deleteSql = deleteSql.trim();
        try {
            cacheMap.remove(key);
            evictionQueue.remove(key);
            dataSource.delete(key, deleteSql);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public long size() {
        lock.readLock().lock();
        try {
            return cacheMap.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            cacheMap.clear();
            evictionQueue.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
