package org.swiftcache.cache;

import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.readingpolicy.SimpleReadPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Cache<K, V> {

    private final long maxSize;

    private final Map<K, V> cacheMap;

    private final Queue<K> evictionQueue;

    private final ReadWriteLock lock;

    private final DataSource<K, V> dataSource;

    private final IEvictionStrategy<K, V> evictionStrategy;

    private final IWritingPolicy<K, V> writingPolicy;

    private final IReadingPolicy<K, V> readingPolicy;

    public Cache(long maxSize,
                 DataSource<K, V> dataSource,
                 IEvictionStrategy<K, V> evictionStrategy,
                 IWritingPolicy<K, V> writingPolicy,
                 IReadingPolicy<K, V> readingPolicy) {
        this.maxSize = maxSize;
        this.cacheMap = new LinkedHashMap<>((int) maxSize, 0.75f, true);
        this.evictionQueue = new LinkedList<>();
        this.lock = new ReentrantReadWriteLock();
        this.dataSource = dataSource;
        this.evictionStrategy = evictionStrategy;
        this.writingPolicy = writingPolicy;
        this.readingPolicy = readingPolicy;
    }

    public void put(K key, V value, String storeSql) {
        this.lock.writeLock().lock();
        storeSql = storeSql.trim();
        try {
            if (this.cacheMap.size() >= this.maxSize) {
                K evictedKey = this.evictionStrategy.evict(this.cacheMap, this.evictionQueue);
                if (evictedKey != null) {
                    this.cacheMap.remove(evictedKey);
                    this.evictionQueue.remove(evictedKey);
                }
            }
            this.writingPolicy.write(this.cacheMap, key, value, this.dataSource, storeSql);
            this.evictionQueue.offer(key);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public V get(K key, String fetchSql) {
        this.lock.readLock().lock();
        fetchSql = fetchSql.trim();
        try {
            V value;
            if (getReadingPolicy() instanceof SimpleReadPolicy) {
                value = this.readingPolicy.read(this.cacheMap, key);
            } else {
                value = this.readingPolicy.readWithDataSource(this.cacheMap, key, this.dataSource, fetchSql);
            }
            if (value != null) {
                this.evictionStrategy.access(key, this.evictionQueue);
            }
            return value;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void remove(K key, String deleteSql) {
        this.lock.writeLock().lock();
        deleteSql = deleteSql.trim();
        try {
            this.cacheMap.remove(key);
            this.evictionQueue.remove(key);
            this.dataSource.delete(key, deleteSql);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public long size() {
        this.lock.readLock().lock();
        try {
            return this.cacheMap.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.cacheMap.clear();
            this.evictionQueue.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public IEvictionStrategy<K, V> getEvictionStrategy() {
        this.lock.readLock().lock();
        try {
            return this.evictionStrategy;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public IReadingPolicy<K, V> getReadingPolicy() {
        this.lock.readLock().lock();
        try {
            return this.readingPolicy;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public IWritingPolicy<K, V> getWritingPolicy() {
        this.lock.readLock().lock();
        try {
            return this.writingPolicy;
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
