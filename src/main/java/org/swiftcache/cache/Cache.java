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

/**
 * Represents a generic cache implementation with eviction strategy, writing policy, and reading policy.
 * <p>
 * This class manages a cache of items with a maximum size. It supports operations to add, retrieve, remove, and clear cache entries.
 * </p>
 *
 * @param <K> The type of keys maintained by this cache.
 * @param <V> The type of values maintained by this cache.
 */
public class Cache<K, V> implements ICache<K, V> {

    /**
     * The maximum number of entries allowed in the cache.
     */
    private final long maxSize;

    /**
     * The map storing the cache entries.
     */
    private final Map<K, V> cacheMap;

    /**
     * A queue to manage eviction order of cache entries.
     */
    private final Queue<K> evictionQueue;

    /**
     * Read-write lock to ensure thread safety for cache operations.
     */
    private final ReadWriteLock lock;

    /**
     * Data source interface for retrieving and deleting data from a persistent store.
     */
    private final IDataSource<K, V> dataSource;

    /**
     * Strategy for evicting entries from the cache.
     */
    private final IEvictionStrategy<K, V> evictionStrategy;

    /**
     * Policy for writing entries to the data source.
     */
    private final IWritingPolicy<K, V> writingPolicy;

    /**
     * Policy for reading entries from the data source.
     */
    private final IReadingPolicy<K, V> readingPolicy;

    /**
     * Constructs a new Cache instance.
     *
     * @param maxSize The maximum number of entries the cache can hold.
     * @param dataSource The data source for retrieving and deleting data.
     * @param evictionStrategy The strategy used for evicting entries from the cache.
     * @param writingPolicy The policy for writing entries to the data source.
     * @param readingPolicy The policy for reading entries from the data source.
     */
    public Cache(long maxSize,
                 IDataSource<K, V> dataSource,
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

    /**
     * Adds a new entry to the cache. If the cache is full, an entry is evicted based on the eviction strategy.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @param storeSql The SQL statement used to store the entry in the data source.
     */
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
            this.evictionStrategy.access(key, this.evictionQueue);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves an entry from the cache. If the entry is not in the cache and the fetchSql is provided,
     * it is fetched from the data source.
     *
     * @param key The key whose associated value is to be returned.
     * @param fetchSql The SQL statement used to fetch the entry from the data source.
     * @return The value associated with the specified key, or null if no value is found.
     */
    public V get(K key, String fetchSql) {
        this.lock.readLock().lock();
        fetchSql = fetchSql.trim();
        try {
            V value;
            if (fetchSql.length() == 0) {
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

    /**
     * Removes an entry from the cache and the data source.
     *
     * @param key The key of the entry to be removed.
     * @param deleteSql The SQL statement used to delete the entry from the data source.
     */
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

    /**
     * Returns the number of entries currently in the cache.
     *
     * @return The number of entries in the cache.
     */
    public long size() {
        this.lock.readLock().lock();
        try {
            return this.cacheMap.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.cacheMap.clear();
            this.evictionQueue.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
