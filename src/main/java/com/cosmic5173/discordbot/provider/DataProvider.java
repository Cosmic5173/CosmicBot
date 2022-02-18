package com.cosmic5173.discordbot.provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataProvider {

    private Connection connection;
    private boolean connected = false;

    public void connect(DatabaseConnectValues connectValues) throws SQLException {
        String connectionUrl = "jdbc:mysql://" + connectValues.address + ":" + connectValues.port + "/" + connectValues.database;

        connection = DriverManager.getConnection(connectionUrl, connectValues.username, connectValues.password);
        connected = true; System.out.println("Connected to database successfully.");
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException ignored) { }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return connected;
    }

    public static class DatabaseConnectValues {
        public String address;
        public String username;
        public String password;
        public String database;
        public int port;

        public static DatabaseConnectValues create(String address, String username, String password, String database, int port) {
            DatabaseConnectValues databaseConnectValues = new DatabaseConnectValues();
            databaseConnectValues.address = address;
            databaseConnectValues.username = username;
            databaseConnectValues.password = password;
            databaseConnectValues.database = database;
            databaseConnectValues.port = port;
            return databaseConnectValues;
        }
    }
}
