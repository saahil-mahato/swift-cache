package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.readingpolicy.SimpleReadPolicy;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SimpleReadPolicy class. This class tests the behavior
 * of the Simple Read policy, ensuring that it correctly handles cache hits
 * and misses without interacting with the underlying repository.
 */
@ExtendWith(MockitoExtension.class)
class SimpleReadPolicyTest {

    @InjectMocks
    private SimpleReadPolicy<String, String> simpleReadPolicy;

    private Map<String, String> cacheMap;

    @Mock
    private ICacheRepository<String, String> repository;

    /**
     * Sets up the test environment before each test case. Initializes the
     * SimpleReadPolicy and the cache map.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheMap = new HashMap<>();
    }

    /**
     * Tests that a read hit returns the value from the cache without calling
     * the repository.
     */
    @Test
    void testReadHitReturnsValueFromCache() {
        String key = "key1";
        String value = "value1";
        cacheMap.put(key, value); // Simulate cache hit

        String result = simpleReadPolicy.read(cacheMap, key, repository);

        assertEquals(value, result); // Should return value from cache
        verify(repository, never()).get(key); // Repository should not be called
    }

    /**
     * Tests that a read miss returns null when the key is not present in the cache.
     */
    @Test
    void testReadMissReturnsNull() {
        String key = "key1";

        String result = simpleReadPolicy.read(cacheMap, key, repository);

        assertNull(result); // Should return null as key is not in cache
        verify(repository, never()).get(key); // Repository should not be called
    }
}
