package org.swiftcache.writingpolicy;

import org.swiftcache.datasource.DataSource;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class implements the Write-If-Absent writing policy for a cache.
 * In Write-If-Absent policy, the write policy is only performed if the key is not already present in the cache.
 * This policy can be useful for scenarios where updates should only happen once for a specific key,
 * or when there's a need to avoid overwriting existing data in the cache.
 *
 * @param <K> The type of the keys used in the cache.
 * @param <V> The type of the values stored in the cache.
 */
public class WriteIfAbsentPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(WriteIfAbsentPolicy.class.getName());

    @Override
    public V write(Map<K, V> cacheMap, K key, V value, DataSource<K, V> dataSource) {
        if (!cacheMap.containsKey(key)) {
            // Key not present in cache, so write to both cache and data source
            cacheMap.put(key, value);
            dataSource.put(key, value);
            logger.log(Level.INFO, "Written key: {0} to cache and data source (Write-If-Absent)", key);
        } else {
            logger.log(Level.INFO, "Key: {0} already exists in cache (Write-If-Absent)", key);
        }

        return value;
    }
}
