package org.swiftcache.readingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the IReadingPolicy interface that uses the Refresh Ahead
 * policy. This policy retrieves a value from the cache and initiates an asynchronous
 * refresh of that value from the underlying data source (repository) to keep the cache
 * up-to-date.
 *
 * @param <K> the type of keys maintained by this reading policy
 * @param <V> the type of values maintained by this reading policy
 */
public class RefreshAheadPolicy<K, V> implements IReadingPolicy<K, V> {

    private static final Logger logger = Logger.getLogger(RefreshAheadPolicy.class.getName());

    /**
     * Reads the value associated with the specified key from the cache. If the value is
     * found, it initiates a background thread to refresh the value from the repository
     * asynchronously.
     *
     * @param cacheMap the cache map containing the entries
     * @param key the key whose associated value is to be read
     * @param repository the repository to use for refreshing the value
     * @return the value associated with the specified key, or null if not found
     */
    @Override
    public V read(final Map<K, V> cacheMap, final K key, final ICacheRepository<K, V> repository) {
        V value = cacheMap.get(key);

        // Start a background thread to refresh the value asynchronously
        new Thread(() -> {
            V freshValue = repository.get(key);
            cacheMap.put(key, freshValue);
            logger.log(Level.INFO, "Value for key: {0} refreshed in background", key);
        }).start();

        return value;
    }
}
