package main.java.com.project.library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

public class MainMenuController {
    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void goToPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main/resources/com/project/library/fxml/" + fxmlFile));
            Stage stage = getStage(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBooks(ActionEvent event) {
        goToPage(event, "book.fxml");
    }

    public void handleMember(ActionEvent event) {
        goToPage(event, "member.fxml");
    }

    public void handleBorrowing(ActionEvent event) {
        goToPage(event, "borrowing.fxml");
    }

    public void handleReport(ActionEvent event) {
        goToPage(event, "report.fxml");
    }

    public void handleLogout(ActionEvent event) {
        goToPage(event, "loginguest.fxml");
    }
}
