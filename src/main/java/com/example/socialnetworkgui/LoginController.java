package com.example.socialnetworkgui;

import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.repository.db.UserDbRepo;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import com.example.socialnetworkgui.utils.security.PasswordHashing;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void loginButtonOnAction(){
        if(!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()){
            String username = usernameTextField.getText();
            String password = enterPasswordField.getText();
            String hashedPassword = ((UserDbRepo) service.getUserRepo()).findUserPassword(username);
            if(hashedPassword == null){
                loginMessageLabel.setText("Invalid input, please try again.");
                clearFields();
            }
            else {
                if (PasswordHashing.checkPassword(password, hashedPassword)) {
                    User searchedUser = ((UserDbRepo) service.getUserRepo()).findUserLogin(username, hashedPassword);
                    loginMessageLabel.setText("You're logged in.");
                    clearFieldsAndLabel();
                    try {
                        FXMLLoader accountLoader = new FXMLLoader();
                        accountLoader.setLocation(getClass().getResource("views/account.fxml"));
                        AnchorPane root = accountLoader.load();
                        Scene scene = new Scene(root);
                        String css = Objects.requireNonNull(this.getClass().getResource("styles/style.css")).toExternalForm();
                        scene.getStylesheets().add(css);
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        AccountController accountController = accountLoader.getController();
                        String userFirstName = searchedUser.getFirstName();
                        String userLastName = searchedUser.getLastName();
//                        Logger logger = Logger.getLogger(LoginController.class.getName());
//                        logger.log(Level.INFO, "start");
                        List<FriendDTO> friends = service.getAllFriendships(userFirstName,userLastName); //todo
//                        logger.log(Level.INFO, "dupa prietenii");
                        Page page = new Page(userFirstName,userLastName,friends);
                        page.setService(service);
                        accountController.setUp(page,stage,root);
                        stage.setTitle("Circus");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    loginMessageLabel.setText("Invalid input, please try again.");
                    clearFields();
                }
            }
        }
        else{
            loginMessageLabel.setText("Please enter username and password.");
            clearFields();
        }
    }

    public void cancelButtonOnAction(){
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

    public void handleRegisterButton() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/user-register.fxml"));
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Register new user");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            RegisterController registerController = loader.getController();
            registerController.setService(service, dialogStage);
            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
