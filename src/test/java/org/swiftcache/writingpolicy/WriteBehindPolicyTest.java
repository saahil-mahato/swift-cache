package org.swiftcache.writingpolicy;

import org.junit.Test;
import org.swiftcache.datasource.IDataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WriteBehindPolicyTest {

    @Test
    public void testWrite() throws InterruptedException {
        WriteBehindPolicy<String, Integer> policy = new WriteBehindPolicy<String, Integer>();
        Map<String, Integer> cacheMap = new HashMap<String, Integer>();
        IDataSource<String, Integer> mockDataSource = mock(IDataSource.class);

        policy.write(cacheMap, "key1", 42, mockDataSource, "INSERT INTO table (key, value) VALUES (?, ?)");

        assertEquals(Integer.valueOf(42), cacheMap.get("key1"));

        // Wait for the asynchronous write to occur
        Thread.sleep(100);

        verify(mockDataSource).store("key1", 42, "INSERT INTO table (key, value) VALUES (?, ?)");
    }
}
