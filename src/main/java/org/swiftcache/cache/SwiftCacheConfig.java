package org.swiftcache.cache;

public class SwiftCacheConfig {

    private final long maxSize;

    private final String evictionStrategy;

    private final String readPolicy;

    private final String writePolicy;

    public static final String LRUEvictionStrategy = "LRU";
    public static final String FIFOEvictionStrategy = "FIFO";

    public static final String SimpleReadPolicy = "SimpleRead";
    public static final String ReadThroughPolicy = "ReadThrough";
    public static final String RefreshAheadPolicy = "RefreshAhead";

    public static final String WriteAlwaysPolicy = "WriteAlways";
    public static final String WriteBehindPolicy = "WriteBehind";
    public static final String WriteIfAbsentPolicy = "WriteIfAbsent";


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
