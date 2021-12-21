package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.EditFriendRequestController;
import com.example.socialnetworkgui.controller.MessageAlert;
import com.example.socialnetworkgui.domain.FriendDTO;
import com.example.socialnetworkgui.domain.FriendRequestDTO;
import com.example.socialnetworkgui.domain.RequestStatus;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import com.example.socialnetworkgui.utils.events.UserFriendChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class AccountController implements Observer<UserFriendChangeEvent> {

    User loggedUser;
    UserFriendshipDbService service;
    ObservableList<FriendDTO> modelFriend = FXCollections.observableArrayList();
    ObservableList<FriendRequestDTO> modelRequest = FXCollections.observableArrayList();
    @FXML
    Stage accountStage;
    @FXML
    AnchorPane anchorPane;

    @FXML
    TableView<FriendDTO> tableFriends;
    @FXML
    TableColumn<FriendDTO, String> friendsFirstName;
    @FXML
    TableColumn<FriendDTO, String> friendsLastName;
    @FXML
    TableColumn<FriendDTO, LocalDate> friendsSinceDate;
    
    @FXML
    TableView<FriendRequestDTO> tableRequests;
    @FXML
    TableColumn<FriendRequestDTO,String> requestsFirstName;
    @FXML
    TableColumn<FriendRequestDTO,String> requestsLastName;
    @FXML
    TableColumn<FriendRequestDTO,RequestStatus> requestsStatus;
    @FXML
    TableColumn<FriendRequestDTO,String> requestsDate;

//    @FXML
//    Button buttonAdd;
//    @FXML
//    Button buttonRemove;
    @FXML
    Label labelLoggedUser;
    @FXML
    Button buttonFriends;
    @FXML
    Button buttonRequests;
    @FXML
    Button buttonMessages;
    @FXML
    Button buttonLogOut;
    @FXML
    StackPane stackPane;
    @FXML
    Pane paneFriends;
    @FXML
    Pane paneRequests;
    @FXML
    Pane paneMessages;

    public void setUserFriendshipService(UserFriendshipDbService userFriendshipDbService, User user, Stage stage,AnchorPane pane){
        loggedUser = user;
        labelLoggedUser.setText(user.getFirstName() + " " + user.getLastName());
        service = userFriendshipDbService;
        service.addObserver(this);
        accountStage = stage;
        anchorPane = pane;
        initModelFriend();
        initModelRequest();
    }

    public void initialize(){
        // Set up the friends table
        friendsFirstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFriend().getFirstName()));
        friendsLastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFriend().getLastName()));
        friendsSinceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableFriends.setItems(modelFriend);
        // Set up the requests table
        requestsFirstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender().getFirstName()));
        requestsLastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender().getLastName()));
        requestsStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        requestsDate.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        tableRequests.setItems(modelRequest);

        buttonFriends.getStyleClass().add("button");
        buttonRequests.getStyleClass().add("button");
        buttonMessages.getStyleClass().add("button");
        buttonLogOut.getStyleClass().add("button");
    }

    @Override
    public void update(UserFriendChangeEvent userFriendChangeEvent) {
        initModelFriend();
        initModelRequest();
    }

    private void initModelFriend() {
        Iterable<FriendDTO> friendDTOS = service.getAllFriendships(loggedUser.getFirstName(),loggedUser.getLastName());
        List<FriendDTO> friendDTOList = StreamSupport.stream(friendDTOS.spliterator(), false).toList();
        modelFriend.setAll(friendDTOList);
    }

    private void initModelRequest() {
        Iterable<FriendRequestDTO> requests = service.getUsersRequests(loggedUser.getFirstName(),loggedUser.getLastName());
        List<FriendRequestDTO> friendDTOList = StreamSupport.stream(requests.spliterator(), false).toList();
        modelRequest.setAll(friendDTOList);
    }

    @FXML
    public void handleFriendsButton(ActionEvent event){
        paneFriends.toFront();
    }

    public void showFriendEditDialog() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("edit-friend-request.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Friend Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            EditFriendRequestController editFriendRequestController = loader.getController();
            editFriendRequestController.setService(service, dialogStage, loggedUser);
            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddFriend(ActionEvent ev){
        showFriendEditDialog();
    }

    @FXML
    public void handleRemoveFriend(ActionEvent ev){
        FriendDTO selectedFriendDTO = tableFriends.getSelectionModel().selectedItemProperty().getValue();
        if(selectedFriendDTO == null){
            MessageAlert.showErrorMessage(null, "No friend selected!");
        }
        else{
            service.removeFriend(loggedUser.getFirstName(), loggedUser.getLastName(), selectedFriendDTO.getFriend().getFirstName(), selectedFriendDTO.getFriend().getLastName());
            initModelFriend();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend has been removed");
        }
    }

    @FXML
    public void handleRequestsButton(ActionEvent event){
        paneRequests.toFront();
    }

    @FXML
    public void handleMessagesButton(ActionEvent actionEvent) {
        paneMessages.toFront();
    }

    @FXML
    public void handleLogOut(ActionEvent ev){
        accountStage.close();
    }
}
