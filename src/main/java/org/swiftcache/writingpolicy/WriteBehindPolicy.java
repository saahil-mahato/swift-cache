package org.swiftcache.writingpolicy;

import org.swiftcache.cacherepository.ICacheRepository;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the IWritingPolicy interface that uses the Write Behind
 * policy. This policy writes the specified value to the cache immediately and
 * queues the write operation to the underlying data source (repository) to be
 * executed asynchronously in a background thread.
 *
 * @param <K> the type of keys maintained by this writing policy
 * @param <V> the type of values maintained by this writing policy
 */
public class WriteBehindPolicy<K, V> implements IWritingPolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteBehindPolicy.class.getName());

    /**
     * Writes the specified value associated with the specified key to the cache map
     * immediately. The write to the underlying repository is performed asynchronously
     * in a background thread.
     *
     * @param cacheMap the cache map to write the entry to
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @param repository the repository to write the entry to
     * @return the value that was written
     */
    @Override
    public V write(Map<K, V> cacheMap, final K key, final V value, final ICacheRepository<K, V> repository) {
        // Update the cache first
        cacheMap.put(key, value);

        // Update the data source asynchronously in a background thread
        new Thread(() -> {
            repository.put(key, value);
            LOGGER.log(Level.INFO, "Written key: {0} to cache, queued for background write to data source", key);
        }).start();

        return value;
    }
}
