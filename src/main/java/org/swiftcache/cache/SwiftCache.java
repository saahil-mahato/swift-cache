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

/**
 * A generic Cache implementation with configurable eviction, reading, and writing policies.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public class SwiftCache<K, V> {

    private final long maxSize;

    private final Map<K, V> cacheMap;

    private final Queue<K> evictionQueue;

    private final ReadWriteLock lock;

    private final DataSource<K, V> dataSource;

    private final IEvictionStrategy<K, V> evictionStrategy;

    private final IWritingPolicy<K, V> writingPolicy;

    private final IReadingPolicy<K, V> readingPolicy;

    /**
     * Constructs a new Cache instance.
     *
     * @param maxSize          the maximum size of the cache
     * @param dataSource       the data source for reading and writing data
     * @param evictionStrategy the strategy used to evict entries from the cache
     * @param writingPolicy    the policy used for writing entries to the cache
     * @param readingPolicy    the policy used for reading entries from the cache
     */
    public SwiftCache(long maxSize,
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

    /**
     * Puts an entry into the cache, possibly evicting an existing entry if the cache exceeds its maximum size.
     *
     * @param key      the key with which the specified value is to be associated
     * @param value    the value to be associated with the specified key
     * @param storeSql the SQL statement for storing the entry in the data source
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
            this.evictionQueue.offer(key);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves an entry from the cache. If the entry is not present in the cache, it may be fetched from the data source.
     *
     * @param key      the key whose associated value is to be returned
     * @param fetchSql the SQL statement for fetching the entry from the data source
     * @return the value associated with the specified key, or {@code null} if there is no entry for the key
     */
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

    /**
     * Removes an entry from the cache and the data source.
     *
     * @param key       the key whose entry is to be removed from the cache
     * @param deleteSql the SQL statement for deleting the entry from the data source
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
     * Returns the number of entries in the cache.
     *
     * @return the number of entries in the cache
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

    /**
     * Returns the eviction strategy used by the cache.
     *
     * @return the eviction strategy
     */
    public IEvictionStrategy<K, V> getEvictionStrategy() {
        this.lock.readLock().lock();
        try {
            return this.evictionStrategy;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Returns the reading policy used by the cache.
     *
     * @return the reading policy
     */
    public IReadingPolicy<K, V> getReadingPolicy() {
        this.lock.readLock().lock();
        try {
            return this.readingPolicy;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Returns the writing policy used by the cache.
     *
     * @return the writing policy
     */
    public IWritingPolicy<K, V> getWritingPolicy() {
        this.lock.readLock().lock();
        try {
            return this.writingPolicy;
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
