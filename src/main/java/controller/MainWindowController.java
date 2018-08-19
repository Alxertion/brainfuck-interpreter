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
    private boolean dirtyFile = false;
    private Stage primaryStage;

    @FXML
    private TextArea codeTextArea;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private String previousContent;

    @FXML
    private void initialize() {
        codeTextArea.textProperty().addListener((observableValue, initialContent, newContent) -> {
            if (!dirtyFile) {
                previousContent = initialContent;
                dirtyFile = true;
                setTitle();
            } else {
                if (newContent.equals(previousContent)) {
                    dirtyFile = false;
                    setTitle();
                }
            }
        });
    }

    @FXML
    private void newMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void openMenuItemHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = createFileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            openFile(file);
        }
    }

    @FXML
    private void openExampleMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void saveMenuItemHandler(ActionEvent actionEvent) {
        if (currentFile != null) {
            saveCodeToFile(currentFile);
        }
        else {
            saveAsMenuItemHandler(null);
        }
    }

    @FXML
    private void saveAsMenuItemHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = createFileChooser();
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            saveCodeToFile(file);
        }
    }

    @FXML
    private void exitMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void undoMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void redoMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void cutMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void copyMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void pasteMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void runMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void aboutMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    private void setTitle() {
        String title = "";
        if (dirtyFile) {
            title = "* " + title;
        }
        if (currentFile != null) {
            title += currentFile.getName() + " - ";
        }
        title += ApplicationMain.BRAINFUCK_TITLE;
        primaryStage.setTitle(title);
    }

    private void openFile(File file) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Files.readLines(file, Charsets.UTF_8).forEach(x -> stringBuilder.append(x).append("\n"));
            codeTextArea.setText(stringBuilder.toString());
            currentFile = file;
            dirtyFile = false;
            setTitle();
        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("File can not be read!");
        }
    }

    private void saveCodeToFile(File file) {
        try {
            if(!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".bf");
            }
            Files.write(codeTextArea.getText(), file, Charsets.UTF_8);
            dirtyFile = false;
            currentFile = file;
            setTitle();
        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("File can not be written!");
        }
    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Brainfuck Code", "*.bf", "*.b")
        );
        return fileChooser;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
