package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.MessageAlert;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.repository.db.UserDbRepo;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import com.example.socialnetworkgui.utils.events.ChangeEventType;
import com.example.socialnetworkgui.utils.events.UserFriendChangeEvent;
import com.example.socialnetworkgui.utils.security.PasswordHashing;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldUserName;
    @FXML
    private TextField textFieldPassword;

    private UserFriendshipDbService service;
    Stage dialogStage;

    @FXML
    private void initialize(){

    }

    public void setService(UserFriendshipDbService service, Stage stage){
        this.service = service;
        this.dialogStage = stage;
    }

    @FXML
    public void handleRegister() {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String userName = textFieldUserName.getText();
        String password = textFieldPassword.getText();
        String hashedPassword = PasswordHashing.hashPassword(password);
        saveRegistration(firstName,lastName,userName,hashedPassword);
        service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.REGISTRATION,null));
    }

    private void saveRegistration(String firstName, String lastName, String userName, String hashedPassword) {
        try{
            ((UserDbRepo) service.getUserRepo()).saveLogin(firstName,lastName,userName,hashedPassword);
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Account created successfully!");
        }
        catch (IllegalArgumentException | ValidationException e){
            dialogStage.close();
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
