package com.example.socialnetworkgui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class MessageAlert {
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message = new Alert(type);
        message.setHeaderText(header);
        styleAlert(text, message);
        File imageFile = new File("Images/happy_clown_transparent.png");
        Image image = new Image(imageFile.toURI().toString());
        message.setGraphic(new ImageView(image));
        message.initOwner(owner);
        message.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error Message");
        styleAlert(text, message);
        File imageFile = new File("Images/scary_clown_transparent.png");
        Image image = new Image(imageFile.toURI().toString());
        message.setGraphic(new ImageView(image));
        message.showAndWait();
    }

    private static void styleAlert(String text, Alert message) {
        message.setContentText(text);
        DialogPane dialogPane = message.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(MessageAlert.class.getResource("styles/style.css")).toString());
        dialogPane.getStyleClass().add("dialog");
        Button okButton = (Button) message.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: #48bfe3");
    }
}
