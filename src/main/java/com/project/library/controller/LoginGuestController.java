package main.java.com.project.library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.project.library.Main;
import main.java.com.project.library.dao.UserDAO;

public class LoginGuestController {
    @FXML
    private void handleAdmin(ActionEvent event) {
        Main.setRoot("loginauthentication");
    }

    @FXML
    private void handleGuest(ActionEvent event) {
        Main.setRoot("guesthome");
    }
}
