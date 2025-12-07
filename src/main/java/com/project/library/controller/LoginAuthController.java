package main.java.com.project.library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.project.library.Main;
import main.java.com.project.library.dao.UserDAO;

public class LoginAuthController {
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleVerify(ActionEvent event) {
        String password = passwordField.getText();

        if (password.equals("LokalpediaAdminLibrary")) {
            Main.setRoot("login");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Verification Failed!");
            alert.setHeaderText(null);
            alert.setContentText("Password salah!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Main.setRoot("loginguest");
    }
}
