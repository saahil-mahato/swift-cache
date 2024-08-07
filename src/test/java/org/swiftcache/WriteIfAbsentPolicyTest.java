package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.writingpolicy.WriteIfAbsentPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WriteIfAbsentPolicyTest {

    @InjectMocks
    private WriteIfAbsentPolicy<String, String> writeIfAbsentPolicy;

    private Map<String, String> cacheMap;

    @Mock
    private ICacheRepository<String, String> repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheMap = new HashMap<>();
    }

    @Test
    void testWriteWhenKeyIsAbsent() {
        String key = "key1";
        String value = "value1";

        // Simulate repository behavior
        doNothing().when(repository).put(key, value); // No action needed for put

        // Write using the WriteIfAbsentPolicy
        String result = writeIfAbsentPolicy.write(cacheMap, key, value, repository);

        // Verify that the value is written to both cache and repository
        assertEquals(value, result); // Return value should match
        assertEquals(value, cacheMap.get(key)); // Cache should contain the value

        // Use Awaitility to wait for the repository's put to be called
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> verify(repository).put(key, value)); // Repository's put should be called
    }

    @Test
    void testNoWriteWhenKeyIsPresent() {
        String key = "key1";
        String value = "value1";
        cacheMap.put(key, value); // Simulate that the key is already present in the cache

        // Attempt to write using the WriteIfAbsentPolicy
        String result = writeIfAbsentPolicy.write(cacheMap, key, "newValue", repository);

        // Verify that the value is not written to the repository
        assertEquals(value, result); // Return value should match the existing value
        assertEquals(value, cacheMap.get(key)); // Cache should still contain the original value
        verify(repository, never()).put(key, "newValue"); // Repository's put should not be called
    }

    @Test
    void testCacheSizeAfterWrite() {
        String key = "key1";
        String value = "value1";

        // Simulate repository behavior
        doNothing().when(repository).put(key, value); // No action needed for put

        // Write using the WriteIfAbsentPolicy
        writeIfAbsentPolicy.write(cacheMap, key, value, repository);

        // Verify the cache size
        assertEquals(1, cacheMap.size()); // Cache size should be 1
    }
}