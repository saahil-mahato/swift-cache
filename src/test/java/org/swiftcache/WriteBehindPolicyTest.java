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


class WriteBehindPolicyTest {

    @InjectMocks
    private WriteBehindPolicy<String, String> writeBehindPolicy;

    private Map<String, String> cacheMap;

    @Mock
    private ICacheRepository<String, String> repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheMap = new HashMap<>();
    }

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
