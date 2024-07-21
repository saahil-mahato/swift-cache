package org.swiftcache.readingpolicy;

import org.junit.Test;
import org.swiftcache.datasource.IDataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RefreshAheadPolicyTest {

    @Test
    public void testReadWithDataSource() throws InterruptedException {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<String, Integer>(100);
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        IDataSource<String, Integer> mockDataSource = mock(IDataSource.class);

        cacheMap.put("key1", 42);
        when(mockDataSource.fetch("key1", "SELECT value FROM table WHERE key = ?")).thenReturn(43);

        Integer result = policy.readWithDataSource(cacheMap, "key1", mockDataSource, "SELECT value FROM table WHERE key = ?");

        assertEquals(Integer.valueOf(42), result);

        // Wait for the refresh to occur
        Thread.sleep(200);

        verify(mockDataSource).fetch("key1", "SELECT value FROM table WHERE key = ?");
        assertEquals(Integer.valueOf(43), cacheMap.get("key1"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRead() {
        RefreshAheadPolicy<String, Integer> policy = new RefreshAheadPolicy<String, Integer>(100);
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        policy.read(cacheMap, "key1");
    }
}