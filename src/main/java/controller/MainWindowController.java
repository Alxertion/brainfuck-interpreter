package controller;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.ApplicationMain;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

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
                setDirtyFile(true);
            } else {
                if (newContent.equals(previousContent)) {
                    setDirtyFile(false);
                }
            }
        });
    }

    @FXML
    private void newMenuItemHandler(ActionEvent actionEvent) {
        try {
            if (!dirtyFile || saveAndExitDialog()) {
                currentFile = null;
                codeTextArea.setText("");
                inputTextArea.setText("");
                outputTextArea.setText("");
                setDirtyFile(false);
            }
        }
        catch (IOException exception) {
            showFileWriteAlert();
        }
    }

    @FXML
    private void openMenuItemHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = createFileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                openFile(file);
            } catch (IOException exception) {
                showFileReadAlert();
            }
        }
    }

    @FXML
    private void openExampleMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void saveMenuItemHandler(ActionEvent actionEvent) {
        try {
            saveCurrentFile();
        } catch (IOException exception) {
            showFileWriteAlert();
        }
    }

    @FXML
    private void saveAsMenuItemHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = createFileChooser();
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                saveCodeToFile(file);
            } catch (IOException exception) {
                showFileWriteAlert();
            }
        }
    }

    @FXML
    private void exitMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    @FXML
    private void undoMenuItemHandler(ActionEvent actionEvent) {
        codeTextArea.undo();
    }

    @FXML
    private void redoMenuItemHandler(ActionEvent actionEvent) {
        codeTextArea.redo();
    }

    @FXML
    private void cutMenuItemHandler(ActionEvent actionEvent) {
        codeTextArea.cut();
    }

    @FXML
    private void copyMenuItemHandler(ActionEvent actionEvent) {
        codeTextArea.copy();
    }

    @FXML
    private void pasteMenuItemHandler(ActionEvent actionEvent) {
        codeTextArea.paste();
    }

    @FXML
    private void runMenuItemHandler(ActionEvent actionEvent) {
        if (dirtyFile) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setContentText("Current file must be saved to continue. Save?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    saveCurrentFile();
                    runCurrentFile();
                } catch (IOException exception) {
                    showFileWriteAlert();
                }
            }
        }
    }

    @FXML
    private void aboutMenuItemHandler(ActionEvent actionEvent) {
        //TODO
    }

    private boolean saveAndExitDialog() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Current File");
        alert.setHeaderText("Do you want to save the currently opened file?");

        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeDontSave = new ButtonType("Don't save");
        ButtonType buttonTypeCancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeDontSave, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeSave) {
                saveCurrentFile();
                return true;
            } else if (result.get() == buttonTypeDontSave) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setContentText(text);
        alert.show();
    }

    private void showFileReadAlert() {
        showAlert("File can not be read!");
    }

    private void showFileWriteAlert() {
        showAlert("File can not be written!");
    }

    private void saveCurrentFile() throws IOException {
        if (currentFile != null) {
            saveCodeToFile(currentFile);
        } else {
            saveAsMenuItemHandler(null);
        }
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

    private void openFile(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Files.readLines(file, Charsets.UTF_8).forEach(x -> stringBuilder.append(x).append("\n"));
        codeTextArea.setText(stringBuilder.toString());
        currentFile = file;
        setDirtyFile(false);
    }

    private void saveCodeToFile(File file) throws IOException {
        if (!file.getName().contains(".")) {
            file = new File(file.getAbsolutePath() + ".bf");
        }
        Files.write(codeTextArea.getText(), file, Charsets.UTF_8);
        currentFile = file;
        setDirtyFile(false);
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

    public void setDirtyFile(boolean dirtyFile) {
        this.dirtyFile = dirtyFile;
        setTitle();
    }

    private void runCurrentFile() {
        //TODO
    }
}
