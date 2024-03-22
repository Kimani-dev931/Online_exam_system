package org.example;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database_Connection {
        public static Connection getConnection() throws SQLException {
        String url = MainApp.getConnectionString();
        String user = MainApp.getDecryptedUsername();
        String password = MainApp.getDecryptedPassword();
        return DriverManager.getConnection(url, user, password);
    }



}
