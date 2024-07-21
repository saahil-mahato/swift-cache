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
        LRUEvictionStrategy<String, Integer> strategy = new LRUEvictionStrategy<String, Integer>();
        Map<String, Integer> cacheMap = new LinkedHashMap<String, Integer>();
        Queue<String> evictionQueue = new LinkedList<String>();

        evictionQueue.offer("key1");
        evictionQueue.offer("key2");
        evictionQueue.offer("key3");

        String evictedKey = strategy.evict(cacheMap, evictionQueue);

        assertEquals("key1", evictedKey);
        assertEquals(2, evictionQueue.size());
        assertFalse(evictionQueue.contains("key1"));
    }
}
