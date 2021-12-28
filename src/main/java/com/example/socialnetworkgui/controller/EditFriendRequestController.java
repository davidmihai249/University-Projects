package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.RequestException;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditFriendRequestController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;

    private UserFriendshipDbService service;
    Stage dialogStage;
    User loggedUser;

    @FXML
    private void initialize(){

    }

    public void setService(UserFriendshipDbService service, Stage stage, User loggedUser){
        this.service = service;
        this.dialogStage = stage;
        this.loggedUser = loggedUser;
    }

    @FXML
    public void handleSave(){
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        User friend = new User(firstName,lastName);
        saveFriendRequest(loggedUser, friend);
        service.notifyObservers(null);
    }

    private void saveFriendRequest(User loggedUser, User friend) {
        try{
            service.addFriendRequest(loggedUser.getFirstName(), loggedUser.getLastName(), friend.getFirstName(), friend.getLastName());
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend request sent successfully!");
        }
        catch (IllegalArgumentException | ValidationException e){
            dialogStage.close();
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        catch (RequestException e){
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", e.getMessage());
        }
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
