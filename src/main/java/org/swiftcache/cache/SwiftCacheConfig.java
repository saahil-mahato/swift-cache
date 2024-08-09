package org.swiftcache.cache;

/**
 * Configuration class for the SwiftCache, encapsulating settings such as
 * maximum size and policies for eviction, reading, and writing.
 */
public class SwiftCacheConfig {

    private final long maxSize;

    private final String evictionStrategy;

    private final String readPolicy;

    private final String writePolicy;

    /** Constant for the Least Recently Used (LRU) eviction strategy. */
    public static final String LRU_EVICTION_STRATEGY = "LRU";

    /** Constant for the First In, First Out (FIFO) eviction strategy. */
    public static final String FIFO_EVICTION_STRATEGY = "FIFO";

    /** Constant for the Simple Read policy. */
    public static final String SIMPLE_READ_POLICY = "SimpleRead";

    /** Constant for the Read Through policy. */
    public static final String READ_THROUGH_POLICY = "ReadThrough";

    /** Constant for the Refresh Ahead policy. */
    public static final String REFRESH_AHEAD_POLICY = "RefreshAhead";

    /** Constant for the Write Always policy. */
    public static final String WRITE_ALWAYS_POLICY = "WriteAlways";

    /** Constant for the Write Behind policy. */
    public static final String WRITE_BEHIND_POLICY = "WriteBehind";

    /** Constant for the Write If Absent policy. */
    public static final String WRITE_IF_ABSENT_POLICY = "WriteIfAbsent";

    /**
     * Constructs a new SwiftCacheConfig with the specified parameters.
     *
     * @param maxSize the maximum size of the cache
     * @param evictionStrategy the strategy to use for evicting entries
     * @param readPolicy the policy to use for reading entries
     * @param writePolicy the policy to use for writing entries
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
     * @return the maximum size
     */
    public long getMaxSize() {
        return this.maxSize;
    }

    /**
     * Returns the eviction strategy for the cache.
     *
     * @return the eviction strategy
     */
    public String getEvictionStrategy() {
        return this.evictionStrategy;
    }

    /**
     * Returns the reading policy for the cache.
     *
     * @return the reading policy
     */
    public String getReadPolicy() {
        return this.readPolicy;
    }

    /**
     * Returns the writing policy for the cache.
     *
     * @return the writing policy
     */
    public String getWritePolicy() {
        return this.writePolicy;
    }
}
