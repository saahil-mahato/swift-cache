package org.swiftcache.evictionstrategy;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.*;

public class LRUEvictionStrategyTest {

    @Test
    public void testEvict() {
        LRUEvictionStrategy<String, Integer> strategy = new LRUEvictionStrategy<>();
        Map<String, Integer> cacheMap = new LinkedHashMap<>();
        Queue<String> evictionQueue = new LinkedList<>();

        evictionQueue.offer("key1");
        evictionQueue.offer("key2");
        evictionQueue.offer("key3");

        strategy.access("key1", evictionQueue);

        String evictedKey = strategy.evict(cacheMap, evictionQueue);

        assertEquals("key2", evictedKey);
        assertEquals(2, evictionQueue.size());
        assertFalse(evictionQueue.contains("key2"));
    }
}
