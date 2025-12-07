package main.java.com.project.library.dao;

import main.java.com.project.library.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next(); // true kalau ada row
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            // Cek apakah username sudah ada
            if (checkUsernameExists(username)) {
                return false;
            }

            // Simpan user baru ke database
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkUsernameExists(String username) {
        // Query untuk mengecek apakah username sudah ada di database
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        // Implementasi pengecekan
        return false; // ganti dengan logika sebenarnya
    }
}
