package org.swiftcache.cache;

import org.swiftcache.datasource.DataSource;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * This class implements a generic cache with configurable eviction strategy, reading policy, and writing policy.
 *
 * @param <K> The key type for the cache entries.
 * @param <V> The value type for the cache entries.
 */
public class SwiftCache<K, V> {

    private static final Logger LOGGER = Logger.getLogger(SwiftCache.class.getName());

    /**
     * The maximum size of the cache in entries.
     */
    private final long maxSize;

    /**
     * The internal map storing the cache entries.
     */
    private final Map<K, V> cacheMap;

    /**
     * The queue used by the eviction strategy to track access order.
     */
    private final Queue<K> evictionQueue;

    /**
     * The read-write lock for thread safety.
     */
    private final ReadWriteLock lock;

    /**
     * The data source used to fetch and store entries when needed.
     */
    private final DataSource<K, V> dataSource;

    /**
     * The strategy used to determine which entries to evict when the cache reaches its maximum size.
     */
    private final IEvictionStrategy<K, V> evictionStrategy;

    /**
     * The policy used to write entries to the cache and potentially the data source.
     */
    private final IWritingPolicy<K, V> writingPolicy;

    /**
     * The policy used to read entries from the cache and potentially the data source.
     */
    private final IReadingPolicy<K, V> readingPolicy;

    /**
     * Creates a new SwiftCache instance with the specified configuration.
     *
     * @param maxSize        The maximum size of the cache in entries.
     * @param dataSource     The data source used to fetch and store entries.
     * @param evictionStrategy The strategy used to determine which entries to evict.
     * @param writingPolicy  The policy used to write entries to the cache and potentially the data source.
     * @param readingPolicy  The policy used to read entries from the cache and potentially the data source.
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
     * Retrieves a value from the cache for the given key.
     *
     * @param key The key of the entry to retrieve.
     * @return The value associated with the key, or null if the key is not found in the cache.
     */
    public V get(K key) {
        this.lock.readLock().lock();
        try {
            V value = this.readingPolicy.read(this.cacheMap, key, this.dataSource);
            if (value != null) {
                this.evictionStrategy.updateQueue(key, this.evictionQueue);
            }

            LOGGER.info("Get " + key);
            return value;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Stores a key-value pair in the cache.
     *
     * @param key   The key for the entry.
     * @param value The value to store.
     */
    public void put(K key, V value) {
        this.lock.writeLock().lock();
        try {
            if (this.cacheMap.size() >= this.maxSize) {
                this.evictionStrategy.evict(this.cacheMap, this.evictionQueue);
            }
            this.writingPolicy.write(this.cacheMap, key, value, this.dataSource);
            this.evictionStrategy.updateQueue(key, this.evictionQueue);

            LOGGER.info("Put " + key);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Removes an entry from the cache for the given key.
     *
     * @param key The key of the entry to remove.
     */
    public void remove(K key) {
        this.lock.writeLock().lock();
        try {
            this.cacheMap.remove(key);
            this.evictionQueue.remove(key);
            this.dataSource.remove(key);

            LOGGER.info("Remove " + key);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Returns the current size of the cache in entries.
     *
     * @return The number of entries currently in the cache.
     */
    public long size() {
        this.lock.writeLock().lock();
        try {
            return this.cacheMap.size();
        } finally {
            this.lock.writeLock().unlock();
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
     * Retrieves the configured eviction strategy.
     *
     * @return The IEvictionStrategy<K, V> instance used by the cache.
     */
    public IEvictionStrategy<K, V> getEvictionStrategy() {
        return this.evictionStrategy;
    }

    /**
     * Retrieves the configured reading policy.
     *
     * @return The IReadingPolicy<K, V> instance used by the cache.
     */
    public IReadingPolicy<K, V> getReadingPolicy() {
        return this.readingPolicy;
    }

    /**
     * Retrieves the configured writing policy.
     *
     * @return The IWritingPolicy<K, V> instance used by the cache.
     */
    public IWritingPolicy<K, V> getWritingPolicy() {
        return this.writingPolicy;
    }
}
