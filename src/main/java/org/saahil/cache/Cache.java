package org.saahil.cache;

import org.saahil.cache.evictionstrategy.IEvictionStrategy;

import javax.sql.DataSource;
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

    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (cacheMap.size() >= maxSize) {
                K evictedKey = evictionStrategy.evict(cacheMap, evictionQueue);
                if (evictedKey != null) {
                    cacheMap.remove(evictedKey);
                    evictionQueue.remove(evictedKey);
                }
            }
            writingPolicy.write(cacheMap, key, value, dataSource);
            evictionQueue.remove(key);
            evictionQueue.offer(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public V get(K key) {
        lock.readLock().lock();
        try {
            V value = readingPolicy.read(cacheMap, key, dataSource);
            if (value != null) {
                evictionQueue.remove(key);
                evictionQueue.offer(key);
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void remove(K key) {
        lock.writeLock().lock();
        try {
            cacheMap.remove(key);
            evictionQueue.remove(key);
            dataSource.delete(key);
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
