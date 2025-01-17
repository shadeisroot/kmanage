package org.example.kmanage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 600);
        stage.setResizable(false);
        scene.getStylesheets().add
                (Main.class.getResource("maincss.css").toExternalForm());
        stage.setTitle("");
        stage.setScene(scene);
        stage.show();
    }
}
