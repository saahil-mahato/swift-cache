package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.readingpolicy.RefreshAheadPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RefreshAheadPolicyTest {

    @InjectMocks
    private RefreshAheadPolicy<String, String> refreshAheadPolicy;

    private Map<String, String> cacheMap;

    @Mock
    private ICacheRepository<String, String> repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheMap = new HashMap<>();
    }

    @Test
    void testReadHitReturnsValueFromCache() {
        String key = "key1";
        String value = "value1";
        cacheMap.put(key, value); // Simulate cache hit

        String result = refreshAheadPolicy.read(cacheMap, key, repository);

        assertEquals(value, result); // Should return value from cache
        verify(repository, never()).get(key); // Repository should not be called
    }

    @Test
    void testAsynchronousRefresh() {
        String key = "key1";
        String initialValue = "value1";
        String refreshedValue = "value2";

        cacheMap.put(key, initialValue); // Simulate cache hit
        when(repository.get(key)).thenReturn(refreshedValue); // Simulate repository return

        // Start the refresh operation
        refreshAheadPolicy.read(cacheMap, key, repository);

        // Use Awaitility to wait for the cache to be updated with the refreshed value
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> refreshedValue.equals(cacheMap.get(key)));

        // Check that the cache has been updated
        assertEquals(refreshedValue, cacheMap.get(key)); // Ensure cache has the refreshed value
        verify(repository).get(key); // Repository should be called
    }

    @Test
    void testReadMissFetchesFromRepository() {
        String key = "key1";
        String value = "value1";
        when(repository.get(key)).thenReturn(value); // Simulate repository return

        // Start the read operation
        refreshAheadPolicy.read(cacheMap, key, repository);

        // Use Awaitility to wait for the cache to be updated with the fetched value
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertEquals(value, cacheMap.get(key)); // Cache should now contain the value
                });

        // Verify that the repository was called
        verify(repository).get(key);
    }

    @Test
    void testReadMissReturnsNullWhenRepositoryReturnsNull() {
        String key = "key1";
        when(repository.get(key)).thenReturn(null); // Simulate repository returning null

        String result = refreshAheadPolicy.read(cacheMap, key, repository);

        // Verify that the result is null immediately
        assertNull(result); // Should return null

        // Use Awaitility to wait for the cache to remain unchanged
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertNull(cacheMap.get(key)); // Cache should not contain the value
                    verify(repository).get(key); // Repository should be called
                });
    }
}