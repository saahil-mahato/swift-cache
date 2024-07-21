package org.swiftcache.readingpolicy;

import org.junit.Test;
import org.swiftcache.datasource.IDataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReadThroughPolicyTest {

    @Test
    public void testReadWithDataSource() {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        IDataSource<String, Integer> mockDataSource = mock(IDataSource.class);

        when(mockDataSource.fetch("key1", "SELECT value FROM table WHERE key = ?")).thenReturn(42);

        Integer result = policy.readWithDataSource(cacheMap, "key1", mockDataSource, "SELECT value FROM table WHERE key = ?");

        assertEquals(Integer.valueOf(42), result);
        assertEquals(Integer.valueOf(42), cacheMap.get("key1"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRead() {
        ReadThroughPolicy<String, Integer> policy = new ReadThroughPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        policy.read(cacheMap, "key1");
    }
}
