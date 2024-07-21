package org.swiftcache.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.swiftcache.datasource.IDataSource;
import org.swiftcache.evictionstrategy.IEvictionStrategy;
import org.swiftcache.readingpolicy.IReadingPolicy;
import org.swiftcache.writingpolicy.IWritingPolicy;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CacheTest {

    private Cache<String, Integer> cache;
    private IDataSource<String, Integer> mockDataSource;
    private IWritingPolicy<String, Integer> mockWritingPolicy;
    private IReadingPolicy<String, Integer> mockReadingPolicy;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        mockDataSource = mock(IDataSource.class);
        IEvictionStrategy<String, Integer> mockEvictionStrategy = mock(IEvictionStrategy.class);
        mockWritingPolicy = mock(IWritingPolicy.class);
        mockReadingPolicy = mock(IReadingPolicy.class);

        cache = new Cache<String, Integer>(3, mockDataSource, mockEvictionStrategy, mockWritingPolicy, mockReadingPolicy);
    }

    @Test
    public void testPut() {
        cache.put("key1", 1, "INSERT INTO table (key, value) VALUES (?, ?)");
        verify(mockWritingPolicy).write(Matchers.<Map<String, Integer>>any(), eq("key1"), eq(1), eq(mockDataSource), eq("INSERT INTO table (key, value) VALUES (?, ?)"));
    }

    @Test
    public void testGet() {
        when(mockReadingPolicy.read(Matchers.<Map<String, Integer>>any(), eq("key1"))).thenReturn(1);
        Integer result = cache.get("key1", "");
        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testGetWithDataSource() {
        when(mockReadingPolicy.readWithDataSource(Matchers.<Map<String, Integer>>any(), eq("key1"), eq(mockDataSource), eq("SELECT value FROM table WHERE key = ?"))).thenReturn(1);
        Integer result = cache.get("key1", "SELECT value FROM table WHERE key = ?");
        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    public void testRemove() {
        cache.remove("key1", "DELETE FROM table WHERE key = ?");
        verify(mockDataSource).delete("key1", "DELETE FROM table WHERE key = ?");
    }

    @Test
    public void testSize() {
        cache.put("key1", 1, "");
        cache.put("key2", 2, "");
        assertEquals(2, cache.size());
    }

    @Test
    public void testClear() {
        cache.put("key1", 1, "");
        cache.put("key2", 2, "");
        cache.clear();
        assertEquals(0, cache.size());
    }
}