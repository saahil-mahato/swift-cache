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

    private static final String INSERT_SQL = "INSERT INTO test_table (testKey, testValue) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT testValue FROM test_table WHERE testKey = ?";
    private static final String DELETE_SQL = "DELETE FROM test_table WHERE testKey = ?";

    @Before
    public void setUp() throws SQLException {
        connection = TestDatabaseUtil.getConnection();
        dataSource = new DataSource<>(connection);
    }

    private void insertTestData(String key, int value) throws SQLException {
        connection.createStatement().execute(
                String.format("INSERT INTO test_table (testKey, testValue) VALUES ('%s', %d)", key, value)
        );
    }

    @Test
    public void testFetch() throws SQLException {
        insertTestData("key1", 42);

        Integer result = dataSource.fetch("key1", SELECT_SQL);
        assertEquals(Integer.valueOf(42), result);
        TestDatabaseUtil.clearTable(connection);
    }

    @Test
    public void testStore() throws SQLException {
        dataSource.store("key2", 43, INSERT_SQL);

        Integer result = dataSource.fetch("key2", SELECT_SQL);
        assertEquals(Integer.valueOf(43), result);
        TestDatabaseUtil.clearTable(connection);
    }

    @Test
    public void testDelete() throws SQLException {
        insertTestData("key3", 44);

        dataSource.delete("key3", DELETE_SQL);

        Integer result = dataSource.fetch("key3", SELECT_SQL);
        assertNull(result);
        TestDatabaseUtil.clearTable(connection);
    }
}