package org.swiftcache.cache;

public class CacheConfig {
    private final String dbUrl;

    private final String dbUser;

    private final String dbPassword;

    private final long maxSize;

    private final String evictionStrategy;

    private final String readPolicy;

    private final String writePolicy;

    public CacheConfig(String dbUrl, String dbUser, String dbPassword, long maxSize,
                       String evictionStrategy, String readPolicy, String writePolicy) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.maxSize = maxSize;
        this.evictionStrategy = evictionStrategy;
        this.readPolicy = readPolicy;
        this.writePolicy = writePolicy;
    }

    public String getDbUrl() { return dbUrl; }

    public String getDbUser() { return dbUser; }

    public String getDbPassword() { return dbPassword; }

    public long getMaxSize() { return maxSize; }

    public String getEvictionStrategy() { return evictionStrategy; }

    public String getReadPolicy() { return readPolicy; }

    public String getWritePolicy() { return writePolicy; }
}