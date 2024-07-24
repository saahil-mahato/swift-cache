package org.swiftcache.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * DataSource class provides methods to interact with a database.
 * It supports fetching, storing, and deleting entries based on SQL queries.
 *
 * @param <K> the type of keys maintained by this data source
 * @param <V> the type of mapped values
 */
public class DataSource<K, V> {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(DataSource.class.getName());

    /**
     * Constructs a new DataSource instance with the specified database connection.
     *
     * @param connection the database connection to be used by this data source
     */
    public DataSource(Connection connection) {
        this.connection = connection;
    }

    /**
     * Fetches a value from the database based on the provided key and SQL query.
     *
     * @param key the key whose associated value is to be fetched
     * @param sql the SQL query to fetch the value
     * @return the value associated with the specified key, or {@code null} if no value is found
     */
    @SuppressWarnings("unchecked")
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

    /**
     * Stores a key-value pair in the database using the provided SQL query.
     *
     * @param key   the key to be stored
     * @param value the value to be stored
     * @param sql   the SQL query to store the key-value pair
     */
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

    /**
     * Deletes an entry from the database based on the provided key and SQL query.
     *
     * @param key the key whose associated entry is to be deleted
     * @param sql the SQL query to delete the entry
     */
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
