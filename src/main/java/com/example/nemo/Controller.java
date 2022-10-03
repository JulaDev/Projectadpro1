package com.example.nemo;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.outputstream.SplitOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Controller {
    @FXML
    private ListView<String> inputListView;
    @FXML
    private ImageView downloadIcon;
    @FXML
    private ImageView browseButton;
    @FXML
    private Button startButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button extractButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField locationField;
    @FXML
    private CheckBox pass_toggle;
    @FXML
    private CheckBox setPasswordButton;
    @FXML
    private PasswordField passwordHidden;

    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private Alert alert = new Alert(Alert.AlertType.NONE);
    private FileChooser fileChooser = new FileChooser();

    private String nameSecond;
    private String passwordSecond;
    private String locationSecond;



    public void initialize() {

        pass_toggle.setDisable(true);
        passwordHidden.setDisable(true);
        passwordField.setDisable(true);

        setPasswordButton.setOnAction(e ->{
            if(setPasswordButton.isSelected()) {
                pass_toggle.setDisable(false);
                passwordHidden.setDisable(false);
                passwordField.setDisable(false);
            }
        });

        pass_toggle.setOnAction(e -> {
            if (pass_toggle.isSelected()) {
                passwordField.setText(passwordHidden.getText());
                passwordField.setVisible(true);
                passwordHidden.setVisible(false);
                return;
            } else {
                passwordHidden.setText(passwordField.getText());
                passwordHidden.setVisible(true);
                passwordField.setVisible(false);
            }
        });



        //Select Multiple Files
        inputListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        inputListView.setOnDragOver(e -> {
            Dragboard board = e.getDragboard();
            if (board.hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
                downloadIcon.setVisible(false);
            } else {
                e.consume();
            }
        });

        inputListView.setOnDragDropped(e -> {
            Dragboard board = e.getDragboard();
            boolean success = false;
            if (board.hasFiles()) {
                success = true;
                String filePath;
                int total_files = board.getFiles().size();
                for (int i = 0; i < total_files; i++) {
                    File file = board.getFiles().get(i);
                    filePath = file.getAbsolutePath();
                    inputListView.getItems().add(filePath);
                }
            }
            e.setDropCompleted(success);
            e.consume();
        });

        deleteButton.setOnAction(e -> {
            listViewDeleteItems(inputListView);
        });

        searchButton.setOnAction(e -> {
            try {
                listviewSearchItems(inputListView);
                downloadIcon.setVisible(false);
            } catch (Exception ignored) {
            }
        });

        startButton.setOnAction(e -> {
            if (Objects.equals(passwordHidden.getText(), "")) {
                passwordHidden.setText(passwordField.getText());
            }else{
                passwordField.setText(passwordHidden.getText());
            }
            System.out.println(passwordHidden);
            System.out.println(passwordField);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(false);
            boolean checknull = false;

            if (Objects.equals(nameField.getText(), "")) {
                checknull = true;
            }
            if (Objects.equals(passwordHidden.getText(), "")) {
                zipParameters.setEncryptFiles(false);
            } else {
                zipParameters.setEncryptFiles(true);
            }
            if (Objects.equals(passwordField.getText(), "")) {
                zipParameters.setEncryptFiles(false);
            } else {
                zipParameters.setEncryptFiles(true);
            }
            if (Objects.equals(locationField.getText(), "")) {
                checknull = true;
            }
            if (checknull == true) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Put something first!");
                alert.show();
                throw new NullPointerException("You Missing Something");
            }
            nameSecond = nameField.getText();
            passwordSecond = passwordHidden.getText();
            locationSecond = locationField.getText();

            System.out.println(nameSecond);
            System.out.println(passwordSecond);
            System.out.println(locationSecond);

            //Combine File to Zip
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);

            ObservableList<String> selectedItems = getSelectedItems(inputListView);

            List<File> filesToAdd = new ArrayList<File>();

            for (int i = 0; i < selectedItems.size(); i++) {
                filesToAdd.add(new File(((String) selectedItems.get(i))));
            }

            System.out.println(filesToAdd);

            String sumLocation = (locationSecond + "/" + nameSecond + ".zip");

            //Set Password in ZipFile
            ZipFile zipFile = new ZipFile(sumLocation, passwordSecond.toCharArray());
            try {
                zipFile.addFiles(filesToAdd, zipParameters);
                zipFile.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            passwordHidden.setText("");
            passwordField.setText("");
        });

        browseButton.setOnMouseClicked(e -> {
            File selectDirectory = directoryChooser.showDialog(Launch.stage);
            if (selectDirectory != null) {
                locationField.setText(selectDirectory.getAbsolutePath());
            }
        });

        extractButton.setOnAction(e -> {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(false);
            boolean checknull = false;

            if (Objects.equals(nameField.getText(), "")) {
                checknull = true;
            }
            if (Objects.equals(locationField.getText(), "")) {
                checknull = true;
            }
            if (checknull == true) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Put something first!");
                alert.show();
                throw new NullPointerException("You Missing Something");
            }
            nameSecond = nameField.getText();
            passwordSecond = passwordHidden.getText();
            locationSecond = locationField.getText();

            String sumLocation = locationSecond + "/" + nameSecond;

            File theDir = new File(sumLocation);

            if (!theDir.exists()) {
                theDir.mkdirs();
            } else {
                System.out.println("This directory already exists");
            }

            String filepathSecond = (String) getSelectedItems(inputListView).get(0);
            ZipFile zipFile = new ZipFile(new File(filepathSecond));

            try {
                zipFile.extractAll(sumLocation);
            } catch (ZipException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    private void listViewDeleteItems(ListView<String> inputListView){
        ObservableList<Integer> selectList;
        selectList = inputListView.getSelectionModel().getSelectedIndices();
        if(selectList.size() == 0){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Select Something!");
            alert.show();
        }
        else {
            for (int i = selectList.size() - 1; i >= 0; i--) {
                inputListView.getItems().remove((int)selectList.get(i));
            }

        }
    }

    private void listviewSearchItems (ListView<String> sampleListView) throws Exception {
        fileChooser.setTitle("Search Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));

        List<File> selectedFile = fileChooser.showOpenMultipleDialog(Launch.stage);

        for (int i = 0; i < selectedFile.size(); i++) {
            sampleListView.getItems().add(selectedFile.get(i).getAbsolutePath());
        }
    }

    private ObservableList<String> getSelectedItems(ListView<String> sampleListView) {
        ObservableList<String> topics;
        return topics = sampleListView.getSelectionModel().getSelectedItems();
    }
}
