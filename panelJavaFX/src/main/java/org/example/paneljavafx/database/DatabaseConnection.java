package org.example.paneljavafx.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3307/panel?allowPublicKeyRetrieval=true&useSSL=false";

    private static final String USER = "root";
    private static final String PASSWORD = "root1234";

    private static Connection connection;

    public static Connection getConnection() {

        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error conectando a la base de datos", e);
        }

        System.out.println("USER: " + USER);
        System.out.println("PASS: " + PASSWORD);

        return connection;
    }
}