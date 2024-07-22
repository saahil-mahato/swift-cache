package org.swiftcache.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Provides a data source implementation for interacting with a relational database.
 * <p>
 * This class uses JDBC to execute SQL statements for fetching, storing, and deleting data in a database.
 * It relies on a {@link Connection} object to perform database operations.
 * </p>
 *
 * @param <K> The type of keys used for database operations.
 * @param <V> The type of values used for database operations.
 */
public class DataSource<K, V> implements IDataSource<K, V> {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(DataSource.class.getName());

    /**
     * Constructs a new DataSource instance with the specified database connection.
     *
     * @param connection The database connection to be used for executing SQL statements.
     */
    public DataSource(Connection connection) {
        this.connection = connection;
    }

    /**
     * Fetches a value associated with the specified key from the database.
     * <p>
     * Executes a SQL query to retrieve the value for the given key.
     * </p>
     *
     * @param key The key whose associated value is to be fetched.
     * @param sql The SQL query to be executed, which should use a parameter for the key.
     * @return The value associated with the specified key, or null if no value is found or an error occurs.
     */
    @SuppressWarnings("unchecked")
    public V fetch(K key, String sql) {
        if (sql.length() < 1) {
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
     * Stores a key-value pair in the database.
     * <p>
     * Executes a SQL update statement to store the provided key and value.
     * </p>
     *
     * @param key The key to be stored.
     * @param value The value to be associated with the key.
     * @param sql The SQL update statement to be executed, which should use parameters for the key and value.
     */
    public void store(K key, V value, String sql) {
        if (sql.length() < 1) {
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
     * Deletes the entry associated with the specified key from the database.
     * <p>
     * Executes a SQL update statement to remove the entry for the given key.
     * </p>
     *
     * @param key The key whose associated entry is to be deleted.
     * @param sql The SQL update statement to be executed, which should use a parameter for the key.
     */
    public void delete(K key, String sql) {
        if (sql.length() < 1) {
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
