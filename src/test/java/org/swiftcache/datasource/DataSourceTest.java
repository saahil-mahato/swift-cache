package org.swiftcache.datasource;

import org.junit.Before;
import org.junit.Test;
import org.swiftcache.util.TestDatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DataSourceTest {

    private DataSource<String, Integer> dataSource;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<String, Integer>(connection);
    }

    @Test
    public void testFetch() throws SQLException {
        // Insert test data
        connection.createStatement().execute("INSERT INTO test_table (key, value) VALUES ('key1', 42)");

        Integer result = dataSource.fetch("key1", "SELECT value FROM test_table WHERE key = ?");
        assertEquals(Integer.valueOf(42), result);
    }

    @Test
    public void testStore() {
        dataSource.store("key2", 43, "INSERT INTO test_table (key, value) VALUES (?, ?)");

        Integer result = dataSource.fetch("key2", "SELECT value FROM test_table WHERE key = ?");
        assertEquals(Integer.valueOf(43), result);
    }

    @Test
    public void testDelete() throws SQLException {
        // Insert test data
        connection.createStatement().execute("INSERT INTO test_table (key, value) VALUES ('key3', 44)");

        dataSource.delete("key3", "DELETE FROM test_table WHERE key = ?");

        Integer result = dataSource.fetch("key3", "SELECT value FROM test_table WHERE key = ?");
        assertNull(result);
    }
}