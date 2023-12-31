package com.example.deepthort;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("employee-view.fxml"));
        Image icon = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("icons.png"))); // Иконка окна
        Scene scene = new Scene(fxmlLoader.load(), 901, 443);
        stage.setTitle("Работа со сотрудниками организации");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}