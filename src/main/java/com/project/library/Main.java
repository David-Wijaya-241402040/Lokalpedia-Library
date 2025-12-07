package main.java.com.project.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/main/resources/com/project/library/fxml/loginguest.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        Image icons = new Image(getClass().getResourceAsStream("/main/resources/com/project/library/images/lokalicon.png"));
        stage.getIcons().add(icons);
        stage.setTitle("Lokalpedia Library");
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/main/resources/com/project/library/fxml/" + fxml + ".fxml"));
            Scene scene = new Scene(loader.load());
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
