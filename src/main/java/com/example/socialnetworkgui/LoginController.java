package com.example.socialnetworkgui;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.repository.db.UserDbRepo;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private UserFriendshipDbService service;
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
        if(!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()){
            String username = usernameTextField.getText();
            String password = enterPasswordField.getText();
            User searchedUser = ((UserDbRepo) service.getUserRepo()).findUserLogin(username,password);
            if(searchedUser != null){
                loginMessageLabel.setText("You're logged in.");
                clearFieldsAndLabel();
                try{
                    FXMLLoader accountLoader = new FXMLLoader();
                    accountLoader.setLocation(getClass().getResource("account.fxml"));
                    AnchorPane root = accountLoader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    AccountController accountController = accountLoader.getController();
                    accountController.setUserFriendshipService(service, searchedUser, stage);
                    stage.show();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else{
                loginMessageLabel.setText("Invalid input, please try again.");
                clearFields();
            }
        }
        else{
            loginMessageLabel.setText("Please enter username and password.");
            clearFields();
        }
    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setUserFriendshipService(UserFriendshipDbService dbService) {
        this.service = dbService;
    }

    private void clearFields(){
        usernameTextField.setText("");
        enterPasswordField.setText("");
    }

    private void clearFieldsAndLabel(){
        loginMessageLabel.setText("");
        clearFields();
    }
}
