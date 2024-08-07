package org.swiftcache.cache;


public class SwiftCacheConfig {

    private final long maxSize;

    private final String evictionStrategy;

    private final String readPolicy;

    private final String writePolicy;

    public static final String LRU_EVICTION_STRATEGY = "LRU";

    public static final String FIFO_EVICTION_STRATEGY = "FIFO";

    public static final String SIMPLE_READ_POLICY = "SimpleRead";

    public static final String READ_THROUGH_POLICY = "ReadThrough";

    public static final String REFRESH_AHEAD_POLICY = "RefreshAhead";

    public static final String WRITE_ALWAYS_POLICY = "WriteAlways";

    public static final String WRITE_BEHIND_POLICY = "WriteBehind";

    public static final String WRITE_IF_ABSENT_POLICY = "WriteIfAbsent";

    public SwiftCacheConfig(long maxSize,
                            String evictionStrategy, String readPolicy, String writePolicy) {
        this.maxSize = maxSize;
        this.evictionStrategy = evictionStrategy;
        this.readPolicy = readPolicy;
        this.writePolicy = writePolicy;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public String getEvictionStrategy() {
        return evictionStrategy;
    }

    public String getReadPolicy() {
        return readPolicy;
    }

    public String getWritePolicy() {
        return writePolicy;
    }
}
