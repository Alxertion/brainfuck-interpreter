package main;

import controller.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationMain extends Application {
    public static String INTERPRETER_VERSION = "v0.1.0";
    public static String INTERPRETER_TITLE = "Brainfuck Interpreter " + INTERPRETER_VERSION;
    public static String REPOSITORY_LINK = "https://github.com/Alxertion/brainfuck-interpreter/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        MainWindowController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle(INTERPRETER_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
