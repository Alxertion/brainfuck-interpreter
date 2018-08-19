package controller;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import interpreter.FileInterpreter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.ApplicationMain;
import memory.Memory;
import memory.Memory8;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.*;

public class MainWindowController {
    private File currentFile = null;
    private boolean dirtyFile = false;
    private Stage primaryStage;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    private TextArea codeTextArea;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private String previousContent;
    @FXML
    private MenuItem runMenuItem;

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
    private void newMenuItemHandler() {
        try {
            if (!dirtyFile || saveAndExitDialog()) {
                currentFile = null;
                codeTextArea.setText("");
                inputTextArea.setText("");
                outputTextArea.setText("");
                setDirtyFile(false);
            }
        } catch (IOException exception) {
            showFileWriteAlert();
        }
    }

    @FXML
    private void openMenuItemHandler() {
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
    private void openExampleMenuItemHandler() {
        //TODO
    }

    @FXML
    private void saveMenuItemHandler() {
        try {
            saveCurrentFile();
        } catch (IOException exception) {
            showFileWriteAlert();
        }
    }

    @FXML
    private void saveAsMenuItemHandler() {
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
    private void exitMenuItemHandler() {
        try {
            if (dirtyFile) {
                if (!saveAndExitDialog()) {
                    return;
                }
            }
            primaryStage.close();
            System.exit(0);
        } catch (IOException exception) {
            showFileWriteAlert();
        }
    }

    @FXML
    private void undoMenuItemHandler() {
        try {
            codeTextArea.undo();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @FXML
    private void redoMenuItemHandler() {
        try {
            codeTextArea.redo();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @FXML
    private void cutMenuItemHandler() {
        try {
            codeTextArea.cut();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @FXML
    private void copyMenuItemHandler() {
        try {
            codeTextArea.copy();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @FXML
    private void pasteMenuItemHandler() {
        try {
            codeTextArea.paste();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @FXML
    private void runMenuItemHandler() {
        if (dirtyFile || currentFile == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setContentText("Current file must be saved to continue. Save?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    saveCurrentFile();
                    if (currentFile == null) {
                        return;
                    }
                    runCurrentFile();
                } catch (IOException exception) {
                    showFileWriteAlert();
                }
            }
        }
        else {
            runCurrentFile();
        }
    }

    @FXML
    private void aboutMenuItemHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(ApplicationMain.INTERPRETER_TITLE);

        Hyperlink link = new Hyperlink();
        link.setText("Repository");
        link.setOnAction(event -> {
            try {
                openWebpage(new URL(ApplicationMain.REPOSITORY_LINK));
            } catch (MalformedURLException ignored) {
            }
        });
        link.setBorder(Border.EMPTY);
        link.setPadding(new Insets(0, 0, 0, 0));

        VBox contentPanel = new VBox();
        contentPanel.getChildren().add(new Label("Copyright © Cosarca Alexandru, 2018"));
        HBox linkHBox = new HBox();
        linkHBox.getChildren().add(new Label("Github link: "));
        linkHBox.getChildren().add(link);
        contentPanel.getChildren().add(linkHBox);
        contentPanel.getChildren().add(new Label("Licensed under the MIT License."));

        alert.getDialogPane().setContent(contentPanel);
        alert.showAndWait();
    }

    private void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception exception) {
                showAlert("Could not open link repository link " + ApplicationMain.REPOSITORY_LINK);
            }
        }
    }

    private void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException exception) {
            showAlert("Could not open link repository link " + ApplicationMain.REPOSITORY_LINK);
        }
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
        alert.showAndWait();
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
            saveAsMenuItemHandler();
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
        title += ApplicationMain.INTERPRETER_TITLE;
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
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(event -> {
            try {
                if (dirtyFile) {
                    if (!saveAndExitDialog()) {
                        event.consume();
                        return;
                    }
                }
                Platform.setImplicitExit(true);
                System.exit(0);
            } catch (IOException exception) {
                event.consume();
                showFileWriteAlert();
            }
        });
    }

    private void setDirtyFile(boolean dirtyFile) {
        this.dirtyFile = dirtyFile;
        setTitle();
    }

    private void runCurrentFile() {
        runMenuItem.setDisable(true);
        inputTextArea.setEditable(false);
        Memory memory = new Memory8();
        FileInterpreter fileInterpreter = new FileInterpreter(currentFile, memory, inputTextArea.getText());
        BlockingQueue<Character> characterQueue = new LinkedBlockingQueue<>();
        OutputStream outputStream = new OutputQueue(characterQueue);
        executorService.submit(() -> {
            fileInterpreter.run(outputStream);
            inputTextArea.setEditable(true);
            runMenuItem.setDisable(false);
        });
        executorService.submit(() -> {
            Platform.runLater(() -> outputTextArea.setText(""));
            outputTextArea.setText("");
            while (fileInterpreter.isRunning() || !characterQueue.isEmpty()) {
                try {
                    char character = characterQueue.take();
                    Platform.runLater(() -> outputTextArea.appendText(String.valueOf(character)));
                } catch (InterruptedException ignored) {
                }
            }
            Platform.runLater(() -> {
                outputTextArea.appendText("\n");
                outputTextArea.appendText("Finished in " + fileInterpreter.getTimeInMilliseconds() + "ms (" + fileInterpreter.getTimeInSeconds() + "s)" + "\n");
                outputTextArea.appendText("Number of executed instructions: " + fileInterpreter.getFormattedInstructionCount());
            });
        });
    }
}
