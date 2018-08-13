package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Brainfuck Interpreter v1.0.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
