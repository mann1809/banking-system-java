package com.youtube.bank.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Wrapper;

public class Database {
    private static final String url = "jdbc:postgresql://localhost:5432/bank";
    private static final String username="postgres";
    private static final String password="bebubebu";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
}
