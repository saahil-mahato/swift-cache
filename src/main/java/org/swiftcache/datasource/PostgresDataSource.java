package org.swiftcache.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PostgresDataSource<K, V> implements IDataSource<K, V> {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(PostgresDataSource.class.getName());

    public PostgresDataSource(Connection connection) {
        this.connection = connection;
    }

    public V fetch(K key, String sql) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setObject(1, key);
            rs = statement.executeQuery();
            if (rs.next()) {
                @SuppressWarnings("unchecked")
                V value = (V) rs.getObject(1);
                return value;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while executing the database operation", e);
        }
        return null;
    }

    public void store(K key, V value, String sql) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setObject(1, key);
            statement.setObject(2, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while executing the database operation.", e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database resources.", e);
            }
        }
    }

    public void delete(K key, String sql) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setObject(1, key);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while executing the database operation.", e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database resources.", e);
            }
        }
    }
}
