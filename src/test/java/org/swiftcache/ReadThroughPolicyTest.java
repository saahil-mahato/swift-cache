package org.swiftcache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swiftcache.cacherepository.ICacheRepository;
import org.swiftcache.readingpolicy.ReadThroughPolicy;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReadThroughPolicyTest {

    @InjectMocks
    private ReadThroughPolicy<String, String> readThroughPolicy;

    private Map<String, String> cacheMap;

    @Mock
    private ICacheRepository<String, String> repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        cacheMap = new HashMap<>();
    }

    @Test
    void testReadHitReturnsValueFromCache() {
        String key = "key1";
        String value = "value1";
        cacheMap.put(key, value); // Simulate cache hit

        String result = readThroughPolicy.read(cacheMap, key, repository);

        assertEquals(value, result); // Should return value from cache
        verify(repository, never()).get(key); // Repository should not be called
    }

    @Test
    void testReadMissFetchesFromRepository() {
        String key = "key1";
        String value = "value1";
        when(repository.get(key)).thenReturn(value); // Simulate repository return

        String result = readThroughPolicy.read(cacheMap, key, repository);

        assertEquals(value, result); // Should return value from repository
        assertEquals(value, cacheMap.get(key)); // Cache should now contain the value
        verify(repository).get(key); // Repository should be called
    }

    @Test
    void testCachePopulationOnReadMiss() {
        String key = "key1";
        String value = "value1";
        when(repository.get(key)).thenReturn(value); // Simulate repository return

        readThroughPolicy.read(cacheMap, key, repository); // Perform read

        assertEquals(value, cacheMap.get(key)); // Ensure cache has the value
    }

    @Test
    void testReadMissReturnsNullWhenRepositoryReturnsNull() {
        String key = "key1";
        when(repository.get(key)).thenReturn(null); // Simulate repository returning null

        String result = readThroughPolicy.read(cacheMap, key, repository);

        assertNull(result); // Should return null
        assertNull(cacheMap.get(key)); // Cache should not contain the value
        verify(repository).get(key); // Repository should be called
    }
}