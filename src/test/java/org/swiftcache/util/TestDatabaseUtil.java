package org.swiftcache.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseUtil {
    private static final String JDBC_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS test_table (testKey VARCHAR(255) PRIMARY KEY, testValue INTEGER)";
    private static final String CLEAR_SQL = "DELETE FROM test_table";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        createTable(connection);
        return connection;
    }

    public static void createTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_TABLE_SQL);
    }

    public static void clearTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CLEAR_SQL);
    }
}