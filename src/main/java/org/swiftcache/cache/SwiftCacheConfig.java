package org.swiftcache.cache;

/**
 * Configuration class for SwiftCache.
 * This class encapsulates the configuration parameters for a SwiftCache instance,
 * including maximum size, eviction strategy, read policy, and write policy.
 */
public class SwiftCacheConfig {

    /**
     * The maximum size of the cache in entries.
     */
    private final long maxSize;

    /**
     * The eviction strategy to use for the cache.
     */
    private final String evictionStrategy;

    /**
     * The read policy to use for the cache.
     */
    private final String readPolicy;

    /**
     * The write policy to use for the cache.
     */
    private final String writePolicy;

    /**
     * Constant representing the LRU eviction strategy.
     */
    public static final String LRU_EVICTION_STRATEGY = "LRU";

    /**
     * Constant representing the FIFO eviction strategy.
     */
    public static final String FIFO_EVICTION_STRATEGY = "FIFO";

    /**
     * Constant representing the simple read policy.
     */
    public static final String SIMPLE_READ_POLICY = "SimpleRead";

    /**
     * Constant representing the read-through policy.
     */
    public static final String READ_THROUGH_POLICY = "ReadThrough";

    /**
     * Constant representing the refresh-ahead policy.
     */
    public static final String REFRESH_AHEAD_POLICY = "RefreshAhead";

    /**
     * Constant representing the write-always policy.
     */
    public static final String WRITE_ALWAYS_POLICY = "WriteAlways";

    /**
     * Constant representing the write-behind policy.
     */
    public static final String WRITE_BEHIND_POLICY = "WriteBehind";

    /**
     * Constant representing the write-if-absent policy.
     */
    public static final String WRITE_IF_ABSENT_POLICY = "WriteIfAbsent";

    /**
     * Constructs a new SwiftCacheConfig instance with the specified parameters.
     *
     * @param maxSize        The maximum size of the cache.
     * @param evictionStrategy The eviction strategy to use.
     * @param readPolicy     The read policy to use.
     * @param writePolicy    The write policy to use.
     */
    public SwiftCacheConfig(long maxSize,
                            String evictionStrategy, String readPolicy, String writePolicy) {
        this.maxSize = maxSize;
        this.evictionStrategy = evictionStrategy;
        this.readPolicy = readPolicy;
        this.writePolicy = writePolicy;
    }

    /**
     * Returns the maximum size of the cache.
     *
     * @return The maximum size of the cache.
     */
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the eviction strategy to use for the cache.
     *
     * @return The eviction strategy.
     */
    public String getEvictionStrategy() {
        return evictionStrategy;
    }

    /**
     * Returns the read policy to use for the cache.
     *
     * @return The read policy.
     */
    public String getReadPolicy() {
        return readPolicy;
    }

    /**
     * Returns the write policy to use for the cache.
     *
     * @return The write policy.
     */
    public String getWritePolicy() {
        return writePolicy;
    }
}
