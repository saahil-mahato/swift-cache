package org.swiftcache.cache;

/**
 * Configuration class for setting up the cache.
 */
public class SwiftCacheConfig {
    private final String dbUrl;

    private final String dbUser;

    private final String dbPassword;

    private final long maxSize;

    private final String evictionStrategy;

    private final String readPolicy;

    private final String writePolicy;

    /**
     * Constructs a new CacheConfig instance with the specified parameters.
     *
     * @param dbUrl             the URL of the database
     * @param dbUser            the database user name
     * @param dbPassword        the database password
     * @param maxSize           the maximum size of the cache
     * @param evictionStrategy  the eviction strategy to be used by the cache
     * @param readPolicy        the read policy to be used by the cache
     * @param writePolicy       the write policy to be used by the cache
     */
    public SwiftCacheConfig(String dbUrl, String dbUser, String dbPassword, long maxSize,
                            String evictionStrategy, String readPolicy, String writePolicy) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.maxSize = maxSize;
        this.evictionStrategy = evictionStrategy;
        this.readPolicy = readPolicy;
        this.writePolicy = writePolicy;
    }

    /**
     * Returns the URL of the database.
     *
     * @return the database URL
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * Returns the database user name.
     *
     * @return the database user name
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Returns the database password.
     *
     * @return the database password
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Returns the maximum size of the cache.
     *
     * @return the maximum size of the cache
     */
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the eviction strategy used by the cache.
     *
     * @return the eviction strategy
     */
    public String getEvictionStrategy() {
        return evictionStrategy;
    }

    /**
     * Returns the read policy used by the cache.
     *
     * @return the read policy
     */
    public String getReadPolicy() {
        return readPolicy;
    }

    /**
     * Returns the write policy used by the cache.
     *
     * @return the write policy
     */
    public String getWritePolicy() {
        return writePolicy;
    }
}
