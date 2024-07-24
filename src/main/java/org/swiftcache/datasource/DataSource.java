package org.swiftcache.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DataSource<K, V> {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(DataSource.class.getName());

    public DataSource(Connection connection) {
        this.connection = connection;
    }

    public V fetch(K key, String sql) {
        if (sql.isEmpty()) {
            return null;
        }
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = this.connection.prepareStatement(sql);
            statement.setObject(1, key);
            rs = statement.executeQuery();
            if (rs.next()) {
                return (V) rs.getObject(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while executing the database operation", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database resources.", e);
            }
        }
        return null;
    }

    public void store(K key, V value, String sql) {
        if (sql.isEmpty()) {
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = this.connection.prepareStatement(sql);
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
        if (sql.isEmpty()) {
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = this.connection.prepareStatement(sql);
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
