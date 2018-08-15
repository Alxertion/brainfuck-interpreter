package controller;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.ApplicationMain;

import java.io.File;
import java.io.IOException;

public class MainWindowController {
    private File currentFile = null;
    private Stage primaryStage;

    @FXML
    private TextArea codeTextArea;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TextArea inputTextArea;

    @FXML
    private void initialize() {

    }

    @FXML
    private void newMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void openMenuItemHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Brainfuck Code", "*.bf", "*.b")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            openFile(file);
        }
    }

    private void openFile(File file) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Files.readLines(file, Charsets.UTF_8).forEach(x -> stringBuilder.append(x).append("\n"));
            codeTextArea.setText(stringBuilder.toString());
            currentFile = file;
            primaryStage.setTitle(file.getName() + " - " + ApplicationMain.BRAINFUCK_TITLE);
        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("File can not be read!");
        }
    }

    @FXML
    private void openExampleMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void exitMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void undoMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void redoMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void cutMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void copyMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void pasteMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void runMenuItemHandler(ActionEvent actionEvent) {

    }

    @FXML
    private void aboutMenuItemHandler(ActionEvent actionEvent) {

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
