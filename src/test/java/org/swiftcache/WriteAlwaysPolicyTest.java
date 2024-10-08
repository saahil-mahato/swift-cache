package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.writingpolicy.WriteAlwaysPolicy;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the WriteAlwaysPolicy class. This class tests the behavior
 * of the Write Always writing policy, ensuring that it correctly writes values
 * to both the cache and the underlying repository.
 */
@ExtendWith(MockitoExtension.class)
class WriteAlwaysPolicyTest {

    @InjectMocks
    private WriteAlwaysPolicy<String, String> writeAlwaysPolicy;

    private Map<String, String> cacheMap;

    @Mock
    private ICacheRepository<String, String> repository;

    /**
     * Sets up the test environment before each test case. Initializes the
     * WriteAlwaysPolicy and the cache map.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheMap = new HashMap<>();
    }

    /**
     * Tests that writing a value using the WriteAlwaysPolicy updates both
     * the cache and the repository.
     */
    @Test
    void testWriteToCacheAndRepository() {
        String key = "key1";
        String value = "value1";

        // Simulate repository behavior
        doNothing().when(repository).put(anyString(), anyString()); // Use matchers for both parameters

        // Write using the WriteAlwaysPolicy
        String result = writeAlwaysPolicy.write(cacheMap, key, value, repository);

        // Verify that the value is written to both cache and repository
        assertEquals(value, result); // Return value should match
        assertEquals(value, cacheMap.get(key)); // Cache should contain the value
        verify(repository).put(key, value); // Repository's put should be called
    }

    /**
     * Tests that the cache size is updated correctly after a write operation.
     */
    @Test
    void testCacheSizeAfterWrite() {
        String key = "key1";
        String value = "value1";

        // Simulate repository behavior
        doNothing().when(repository).put(anyString(), anyString()); // Use matchers for both parameters

        // Write using the WriteAlwaysPolicy
        writeAlwaysPolicy.write(cacheMap, key, value, repository);

        // Verify the cache size
        assertEquals(1, cacheMap.size()); // Cache size should be 1
    }

    /**
     * Tests the overwrite behavior of the WriteAlwaysPolicy. It ensures that
     * writing a new value to an existing key updates both the cache and the
     * repository.
     */
    @Test
    void testOverwriteBehavior() {
        String key = "key1";
        String initialValue = "value1";
        String newValue = "value2";

        // Simulate repository behavior
        doNothing().when(repository).put(anyString(), anyString()); // Use matchers for both parameters

        // Write the initial value
        writeAlwaysPolicy.write(cacheMap, key, initialValue, repository);

        // Write the new value
        String result = writeAlwaysPolicy.write(cacheMap, key, newValue, repository);

        // Verify that the value is updated in both cache and repository
        assertEquals(newValue, result); // Return value should match the new value
        assertEquals(newValue, cacheMap.get(key)); // Cache should contain the new value
        verify(repository, times(2)).put(eq(key), anyString()); // Repository's put should be called twice
    }
}
