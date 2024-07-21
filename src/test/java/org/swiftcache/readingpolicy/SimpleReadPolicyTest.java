package org.swiftcache.readingpolicy;

import org.junit.Test;
import org.swiftcache.datasource.IDataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SimpleReadPolicyTest {

    @Test
    public void testRead() {
        SimpleReadPolicy<String, Integer> policy = new SimpleReadPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        cacheMap.put("key1", 42);

        Integer result = policy.read(cacheMap, "key1");

        assertEquals(Integer.valueOf(42), result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReadWithDataSource() {
        SimpleReadPolicy<String, Integer> policy = new SimpleReadPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        IDataSource<String, Integer> mockDataSource = mock(IDataSource.class);

        policy.readWithDataSource(cacheMap, "key1", mockDataSource, "SELECT value FROM table WHERE key = ?");
    }
}
