package main.java.com.project.library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.project.library.Main;
import main.java.com.project.library.dao.UserDAO;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal",
                    "Username dan password tidak boleh kosong!");
            return;
        }

        // Validasi panjang password (opsional, tapi direkomendasikan)
        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Password Terlalu Pendek",
                    "Password minimal harus 6 karakter!");
            return;
        }

        // Coba melakukan registrasi
        boolean registrationSuccess = userDAO.register(username, password);

        if (registrationSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil",
                    "Akun berhasil dibuat! Silakan login.");

            // Clear fields setelah registrasi berhasil
            usernameField.clear();
            passwordField.clear();

            // Arahkan ke halaman login
            Main.setRoot("login");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal",
                    "Username sudah digunakan atau terjadi kesalahan!");
        }
    }

    @FXML
    private void handleLogin() {
        Main.setRoot("login");
    }

    // Helper method untuk menampilkan alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}