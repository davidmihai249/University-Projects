package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import com.example.socialnetworkgui.repository.db.UserDbRepo;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private UserDbRepo userDbRepo;
    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File brandingFile = new File("Images/clown.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);
    }

    public void loginButtonOnAction(ActionEvent event){

        if(usernameTextField.getText().isBlank() == false && enterPasswordField.getText().isBlank() == false){
            validateLogin();
        }
        else{
            loginMessageLabel.setText("Please enter username and password.");
        }
    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setUserDbRepo(UserDbRepo userDbRepo) {
        this.userDbRepo = userDbRepo;
    }


    public void validateLogin(){
        String username = usernameTextField.getText();
        String password = enterPasswordField.getText();
        if(userDbRepo.findUserLogin(username,password)){
            loginMessageLabel.setText("You're logged in.");
        }
        else{
            loginMessageLabel.setText("Invalid input, please try again.");
        }
    }
}
