package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.writingpolicy.WriteBehindPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the WriteBehindPolicy class. This class tests the behavior
 * of the Write Behind writing policy, ensuring that it correctly writes values
 * to the cache immediately and asynchronously to the underlying repository.
 */
class WriteBehindPolicyTest {

    @InjectMocks
    private WriteBehindPolicy<String, String> writeBehindPolicy;

    private Map<String, String> cacheMap;

    @Mock
    private ICacheRepository<String, String> repository;

    /**
     * Sets up the test environment before each test case. Initializes the
     * WriteBehindPolicy and the cache map.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheMap = new HashMap<>();
    }

    /**
     * Tests that writing a value using the WriteBehindPolicy updates the cache
     * immediately and triggers an asynchronous write to the repository.
     */
    @Test
    void testWriteToCacheAndAsyncToRepository() {
        String key = "key1";
        String value = "value1";

        // Write using the WriteBehindPolicy
        String result = writeBehindPolicy.write(cacheMap, key, value, repository);

        // Verify that the value is written to the cache immediately
        assertEquals(value, result); // Return value should match
        assertEquals(value, cacheMap.get(key)); // Cache should contain the value

        // Use Awaitility to wait for the asynchronous write to the repository
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    verify(repository).put(key, value); // Repository's put should be called
                });
    }
}
