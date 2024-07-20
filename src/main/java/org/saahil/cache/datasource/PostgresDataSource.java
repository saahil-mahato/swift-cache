package org.saahil.cache.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresDataSource<K, V> implements IDataSource<K, V> {
    private final Connection connection;

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
                return (V) rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
