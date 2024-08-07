package org.swiftcache.cache;

import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.utils.TriFunction;
import org.swiftcache.writingpolicy.IWritingPolicy;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SwiftCache<K, V> {

    private static final Logger logger = Logger.getLogger(SwiftCache.class.getName());

    private final long maxSize;

    private final Map<K, V> cacheMap;

    private final Queue<K> evictionQueue;

    private final ReadWriteLock lock;

    private final IEvictionStrategy<K, V> evictionStrategy;

    private final IWritingPolicy<K, V> writingPolicy;

    private final IReadingPolicy<K, V> readingPolicy;

    public SwiftCache(long maxSize,
                      IEvictionStrategy<K, V> evictionStrategy,
                      IWritingPolicy<K, V> writingPolicy,
                      IReadingPolicy<K, V> readingPolicy) {
        this.maxSize = maxSize;
        this.cacheMap = new ConcurrentHashMap<>((int) maxSize, 0.75f, 5);
        this.evictionQueue = new LinkedList<>();
        this.lock = new ReentrantReadWriteLock();
        this.evictionStrategy = evictionStrategy;
        this.writingPolicy = writingPolicy;
        this.readingPolicy = readingPolicy;
    }

    public V get(ICacheRepository<K, V> repository, K key) {
        this.lock.readLock().lock();
        V value;
        try {
            value = this.readingPolicy.read(this.cacheMap, key, repository);
            this.evictionStrategy.updateQueue(key, this.evictionQueue);

            logger.log(Level.INFO, "Key {0} fetched", key);
        } finally {
            this.lock.readLock().unlock();
        }
        return value;
    }

    public V put(ICacheRepository<K, V> repository, K key, V value) {
        this.lock.writeLock().lock();
        V newValue;
        try {
            if (this.cacheMap.size() >= this.maxSize) {
                this.evictionStrategy.evict(this.cacheMap, this.evictionQueue);
            }
            newValue = this.writingPolicy.write(this.cacheMap, key, value, repository);
            this.evictionStrategy.updateQueue(key, this.evictionQueue);

            logger.log(Level.INFO, "Key {0} inserted", key);
        } finally {
            this.lock.writeLock().unlock();
        }
        return newValue;
    }

    public void remove(ICacheRepository<K, V> repository, K key) {
        this.lock.writeLock().lock();
        try {
            this.cacheMap.remove(key);
            this.evictionQueue.remove(key);
            repository.remove(key);

            logger.log(Level.INFO, "Key {0} removed", key);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public <R> R executeWithCache(ICacheRepository<K,V> repository, K key, V value, TriFunction<ICacheRepository<K, V>, K, V, R> operation) {
        this.lock.writeLock().lock();
        R executionResult;
        try {
            executionResult = repository.executeWithCache(operation, key, value);
        } finally {
            this.lock.writeLock().unlock();
        }
        return executionResult;
    }

    public long size() {
        this.lock.writeLock().lock();
        int size;
        try {
            size = this.cacheMap.size();
        } finally {
            this.lock.writeLock().unlock();
        }
        return size;
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
        return this.evictionStrategy;
    }

    public IReadingPolicy<K, V> getReadingPolicy() {
        return this.readingPolicy;
    }

    public IWritingPolicy<K, V> getWritingPolicy() {
        return this.writingPolicy;
    }
}
