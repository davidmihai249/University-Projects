package com.example.socialnetworkgui;

import com.example.socialnetworkgui.MessageAlert;
import com.example.socialnetworkgui.domain.Event;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.domain.validators.ValidatorEvent;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class EventController {
    @FXML
    TextField nameField;
    @FXML
    DatePicker startDateDatePicker;
    @FXML
    DatePicker endDateDatePicker;
    @FXML
    TextField descriptionTextField;
    @FXML
    TextField locationTextField;
    @FXML
    Button addButton;

    private UserFriendshipDbService service;
    Stage addEventStage;

    User loggedUser;

    @FXML
    private void initialize(){

    }

    public void setService(UserFriendshipDbService service, Stage stage,User loggedUser){
        this.service = service;
        this.addEventStage = stage;
        this.loggedUser = loggedUser;
    }

    public void handleAddButton(){
        String eventName = nameField.getText();
        LocalDate startDateAux = startDateDatePicker.getValue();
        LocalDateTime startDate = startDateAux.atTime(LocalTime.now());
        LocalDate endDateAux = endDateDatePicker.getValue();
        LocalDateTime endDate = endDateAux.atTime(LocalTime.now());
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        Event newEvent = new Event(eventName,startDate,endDate,description,location,"HOST",loggedUser);
        try{
            service.addEvent(newEvent);
            service.addParticipant(newEvent,loggedUser);
            addEventStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Event created successfully!");
        }
        catch (IllegalArgumentException | ValidationException e){
            addEventStage.close();
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}
