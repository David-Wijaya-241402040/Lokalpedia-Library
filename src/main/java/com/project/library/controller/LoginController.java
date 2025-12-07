package main.java.com.project.library.controller;

import main.java.com.project.library.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.project.library.Main;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (userDAO.login(username, password)) {
            Main.setRoot("mainmenu"); // buka halaman utama
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Username atau password salah!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRegister() {
        Main.setRoot("register");
    }
}
