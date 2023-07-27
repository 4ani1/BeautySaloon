package com.example.deepthort;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    public Connection DBConnect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/test_db", "postgres", "12347890");
            System.out.println("Подключение к серверу PostgreSQL прошло успешно.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
