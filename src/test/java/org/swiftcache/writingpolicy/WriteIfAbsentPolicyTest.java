package org.swiftcache.writingpolicy;

import org.junit.Test;
import org.swiftcache.datasource.IDataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WriteIfAbsentPolicyTest {

    @Test
    public void testWriteWhenAbsent() {
        WriteIfAbsentPolicy<String, Integer> policy = new WriteIfAbsentPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        IDataSource<String, Integer> mockDataSource = mock(IDataSource.class);

        policy.write(cacheMap, "key1", 42, mockDataSource, "INSERT INTO table (key, value) VALUES (?, ?)");

        verify(mockDataSource).store("key1", 42, "INSERT INTO table (key, value) VALUES (?, ?)");
        assertEquals(Integer.valueOf(42), cacheMap.get("key1"));
    }

    @Test
    public void testWriteWhenPresent() {
        WriteIfAbsentPolicy<String, Integer> policy = new WriteIfAbsentPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        IDataSource<String, Integer> mockDataSource = mock(IDataSource.class);

        cacheMap.put("key1", 42);
        policy.write(cacheMap, "key1", 43, mockDataSource, "INSERT INTO table (key, value) VALUES (?, ?)");

        verify(mockDataSource, never()).store(anyString(), anyInt(), anyString());
        assertEquals(Integer.valueOf(42), cacheMap.get("key1"));
    }
}