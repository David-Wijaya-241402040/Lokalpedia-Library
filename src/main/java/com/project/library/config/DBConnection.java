package main.java.com.project.library.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarydb",
                    "root",
                    ""
            );
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to DB!");
            return null;
        }
    }

}
