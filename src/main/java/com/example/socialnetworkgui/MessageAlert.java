package com.example.socialnetworkgui;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class MessageAlert {
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message = new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        DialogPane dialogPane = message.getDialogPane();
        dialogPane.getStylesheets().add(MessageAlert.class.getResource("styles/style.css").toString());
        dialogPane.getStyleClass().add("dialog");
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
        message.setContentText(text);
        DialogPane dialogPane = message.getDialogPane();
        dialogPane.getStylesheets().add(MessageAlert.class.getResource("styles/style.css").toString());
        dialogPane.getStyleClass().add("dialog");
        File imageFile = new File("Images/scary_clown_transparent.png");
        Image image = new Image(imageFile.toURI().toString());
        message.setGraphic(new ImageView(image));
        message.showAndWait();
    }
}
