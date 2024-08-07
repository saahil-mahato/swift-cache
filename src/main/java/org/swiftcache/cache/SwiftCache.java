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

/**
 * A custom cache implementation that provides caching functionality with
 * eviction strategies, reading policies, and writing policies.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 */
public class SwiftCache<K, V> {

    private static final Logger logger = Logger.getLogger(SwiftCache.class.getName());

    private final long maxSize;

    private final Map<K, V> cacheMap;

    private final Queue<K> evictionQueue;

    private final ReadWriteLock lock;

    private final IEvictionStrategy<K, V> evictionStrategy;

    private final IWritingPolicy<K, V> writingPolicy;

    private final IReadingPolicy<K, V> readingPolicy;

    /**
     * Constructs a new SwiftCache with the specified maximum size and policies.
     *
     * @param maxSize the maximum number of entries the cache can hold
     * @param evictionStrategy the strategy to use for evicting entries
     * @param writingPolicy the policy to use for writing entries
     * @param readingPolicy the policy to use for reading entries
     */
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

    /**
     * Retrieves an entry from the cache, using the specified repository to fetch the value if not present.
     *
     * @param repository the repository to use for reading the value if not in cache
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key, or null if not found
     */
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

    /**
     * Inserts a new entry into the cache or updates an existing entry.
     *
     * @param repository the repository to use for writing the value
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or null if there was no mapping for the key
     */
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

    /**
     * Removes the entry for a specified key from the cache and the repository.
     *
     * @param repository the repository to remove the value from
     * @param key the key whose mapping is to be removed from the cache
     */
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

    /**
     * Executes an operation with the cache, utilizing the provided repository.
     *
     * @param repository the repository to use for the operation
     * @param key the key to operate on
     * @param value the value to operate with
     * @param operation the operation to execute
     * @param <R> the return type of the operation
     * @return the result of the operation
     */
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

    /**
     * Returns the current size of the cache.
     *
     * @return the number of entries in the cache
     */
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

    /**
     * Clears the cache, removing all entries.
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
     * Returns the eviction strategy used by this cache.
     *
     * @return the eviction strategy
     */
    public IEvictionStrategy<K, V> getEvictionStrategy() {
        return this.evictionStrategy;
    }

    /**
     * Returns the reading policy used by this cache.
     *
     * @return the reading policy
     */
    public IReadingPolicy<K, V> getReadingPolicy() {
        return this.readingPolicy;
    }

    /**
     * Returns the writing policy used by this cache.
     *
     * @return the writing policy
     */
    public IWritingPolicy<K, V> getWritingPolicy() {
        return this.writingPolicy;
    }
}
