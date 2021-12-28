package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.EditFriendRequestController;
import com.example.socialnetworkgui.controller.MessageAlert;
import com.example.socialnetworkgui.domain.FriendDTO;
import com.example.socialnetworkgui.domain.FriendRequestDTO;
import com.example.socialnetworkgui.domain.RequestStatus;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.RequestException;
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
    ObservableList<FriendRequestDTO> modelSentRequest = FXCollections.observableArrayList();
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
    Pane receivedPane;
    @FXML
    Pane sentPane;
    
    @FXML
    TableView<FriendRequestDTO> tableReceivedRequests;
    @FXML
    TableColumn<FriendRequestDTO,String> receivedRequestsFirstName;
    @FXML
    TableColumn<FriendRequestDTO,String> receivedRequestsLastName;
    @FXML
    TableColumn<FriendRequestDTO,RequestStatus> receivedRequestsStatus;
    @FXML
    TableColumn<FriendRequestDTO,String> receivedRequestsDate;

    @FXML
    TableView<FriendRequestDTO> tableSentRequests;
    @FXML
    TableColumn<FriendRequestDTO,String> sentRequestsFirstName;
    @FXML
    TableColumn<FriendRequestDTO,String> sentRequestsLastName;
    @FXML
    TableColumn<FriendRequestDTO,RequestStatus> sentRequestsStatus;
    @FXML
    TableColumn<FriendRequestDTO,String> sentRequestsDate;
    
    @FXML
    Button buttonAdd;
    @FXML
    Button buttonRemove;
    @FXML
    Button buttonApprove;
    @FXML
    Button buttonReject;
    @FXML
    Button buttonSentReq;
    @FXML
    Button buttonReceivedReq;
    @FXML
    Button buttonUnsend;
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
        service.notifyObservers(null);
        accountStage = stage;
        anchorPane = pane;
    }

    public void initialize(){
        // Set up the friends table
        friendsFirstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFriend().getFirstName()));
        friendsLastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFriend().getLastName()));
        friendsSinceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableFriends.setItems(modelFriend);
        // Set up the received requests table
        receivedRequestsFirstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender().getFirstName()));
        receivedRequestsLastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender().getLastName()));
        receivedRequestsStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        receivedRequestsDate.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        tableReceivedRequests.setItems(modelRequest);
        // Set up the sent requests table
        sentRequestsFirstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender().getFirstName()));
        sentRequestsLastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender().getLastName()));
        sentRequestsStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        sentRequestsDate.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        tableSentRequests.setItems(modelSentRequest);

        sentPane.setVisible(false);
        receivedPane.setVisible(false);
    }

    @Override
    public void update(UserFriendChangeEvent userFriendChangeEvent) {
        initModelFriend();
        initModelRequest();
        initModelSentRequest();
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

    private void initModelSentRequest() {
        Iterable<FriendRequestDTO> requests = service.getUsersSentRequests(loggedUser.getFirstName(),loggedUser.getLastName());
        List<FriendRequestDTO> friendDTOList = StreamSupport.stream(requests.spliterator(), false).toList();
        modelSentRequest.setAll(friendDTOList);
    }

    @FXML
    public void handleFriendsButton(ActionEvent event){
        paneFriends.toFront();
    }

    public void showFriendEditDialog() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/edit-friend-request.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Send Friend Request");
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
            service.notifyObservers(null);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend has been removed");
        }
    }

    @FXML
    public void handleRequestsButton(ActionEvent event){
        paneRequests.toFront();
    }

    private void respondToRequest(User sender, RequestStatus status){
        switch (status){
            case APPROVED -> service.respondFriendRequest(sender.getFirstName(),sender.getLastName(),loggedUser.getFirstName(),loggedUser.getLastName(), "APPROVE");
            case REJECTED -> service.respondFriendRequest(sender.getFirstName(),sender.getLastName(),loggedUser.getFirstName(),loggedUser.getLastName(), "REJECT");
        }
        service.notifyObservers(null);
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend request updated!");
    }

    @FXML
    public void handleApproveButton(ActionEvent event){
        FriendRequestDTO selectedRequestDTO = tableReceivedRequests.getSelectionModel().selectedItemProperty().getValue();
        try{
            validateRequestSelection(selectedRequestDTO);
            respondToRequest(selectedRequestDTO.getSender(), RequestStatus.APPROVED);
        }
        catch (RequestException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleRejectButton(ActionEvent event){
        FriendRequestDTO selectedRequestDTO = tableReceivedRequests.getSelectionModel().selectedItemProperty().getValue();
        try{
            validateRequestSelection(selectedRequestDTO);
            respondToRequest(selectedRequestDTO.getSender(), RequestStatus.REJECTED);
        }
        catch (RequestException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void validateRequestSelection(FriendRequestDTO selectedRequest ){
        if(selectedRequest == null){
            throw new RequestException("No friend request selected!");
        }
        else if(!selectedRequest.getStatus().equals(RequestStatus.PENDING)){
            throw new RequestException("Friend request already responded!");
        }
    }

    @FXML
    public void handleSentReqButton(ActionEvent event) {
        sentPane.toFront();
        sentPane.setVisible(true);
        receivedPane.setVisible(false);
    }

    @FXML
    public void handleReceivedReqButton(ActionEvent event) {
        receivedPane.toFront();
        receivedPane.setVisible(true);
        sentPane.setVisible(false);
    }

    @FXML
    public void handleUnsendButton(ActionEvent event) {
        FriendRequestDTO requestDTO = tableSentRequests.getSelectionModel().selectedItemProperty().getValue();
        if(requestDTO == null){
            MessageAlert.showErrorMessage(null, "No friend request selected!");
        }
        else if(!requestDTO.getStatus().equals(RequestStatus.PENDING)){
            MessageAlert.showErrorMessage(null, "Friend request already responded!");
        }
        else{
            User user = requestDTO.getSender();
            service.removeFriendRequest(loggedUser.getFirstName(), loggedUser.getLastName(), user.getFirstName(), user.getLastName());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend request unsent successfully!");
            service.notifyObservers(null);
        }
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
