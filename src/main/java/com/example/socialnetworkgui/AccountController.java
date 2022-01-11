package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.EditFriendRequestController;
import com.example.socialnetworkgui.controller.MessageAlert;
import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.domain.validators.RequestException;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import com.example.socialnetworkgui.utils.events.ChangeEventType;
import com.example.socialnetworkgui.utils.events.UserFriendChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    //Chat
    @FXML
    private TableView<Chat> tableChatFriends;
    @FXML
    private TableColumn<Chat,String> chatColumn;
    @FXML
    private ListView<Message> listViewConversation;
    @FXML
    private TextField textFieldMessage;
    @FXML
    private ObservableList<Message> modelMessages = FXCollections.observableArrayList();
    @FXML
    private ObservableList<Chat> modelChat = FXCollections.observableArrayList();
    @FXML
    Chat selectedChat;

    public void setUserFriendshipService(UserFriendshipDbService userFriendshipDbService, User user, Stage stage,AnchorPane pane){
        loggedUser = user;
        labelLoggedUser.setText(user.getFirstName() + " " + user.getLastName());
        service = userFriendshipDbService;
        service.addObserver(this);
        service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.ALL,null));
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
        chatColumn.setCellValueFactory(features ->{
            String chatNameString = features.getValue().getChatName();
            SimpleStringProperty chatName = new SimpleStringProperty(chatNameString);
            User firstUser = service.getUserRepo().findOne(features.getValue().getUsers().get(0));
            if(features.getValue().getUsers().size()==2){
                if(firstUser.getFirstName().equals(loggedUser.getFirstName()) && firstUser.getLastName().equals(loggedUser.getLastName())){
                    User secondUser = service.getUserRepo().findOne(features.getValue().getUsers().get(1));
                    return Bindings.when(chatName.isEmpty())
                            .then(secondUser.getFirstName() + " " + secondUser.getLastName())
                            .otherwise(chatNameString);
                }
                else
                {
                    return Bindings.when(chatName.isEmpty())
                            .then(firstUser.getFirstName() + " " + firstUser.getLastName())
                            .otherwise(chatNameString);
                }
            }
            return Bindings.when(chatName.isEmpty())
                    .then(firstUser.getFirstName() + " " + firstUser.getLastName())
                    .otherwise(chatNameString);

        });
        tableChatFriends.setItems(modelChat);
        listViewConversation.setItems(modelMessages);
        sentPane.setVisible(false);
        receivedPane.setVisible(false);
    }

    @Override
    public void update(UserFriendChangeEvent userFriendChangeEvent) {
        switch (userFriendChangeEvent.getType()) {
            case FRIEND_ADD: {
                initModelSentRequest();
            }
            case FRIEND_REMOVE: {
                initModelFriend();
            }
            case REQUEST_UNSEND: {
                initModelSentRequest();
            }
            case REQUEST_APPROVE: {
                initModelFriend();
                initModelReceivedRequest();
            }
            case REQUEST_REJECT: {
                initModelReceivedRequest();
            }
            case MESSAGE: {
                initModelChat();
                initModelChatsList();
            }
            case ALL: {
                initModelChatsList();
                initModelChat();
                initModelFriend();
                initModelReceivedRequest();
                initModelSentRequest();
            }
            case REGISTRATION: {

            }
        }
    }

    private void initModelChatsList(){
        Iterable<Chat> chats = service.getAllChats();
        List<Chat> chatsList = StreamSupport.stream(chats.spliterator(),false).toList();
        List<Chat> finalChats = new ArrayList<>();
        chatsList.forEach(chat -> {
            if(chat.getUsers().contains(loggedUser.getId())){
                finalChats.add(chat);
            }
        });
        modelChat.setAll(finalChats);
    }

    private void initModelFriend() {
        Iterable<FriendDTO> friendDTOS = service.getAllFriendships(loggedUser.getFirstName(),loggedUser.getLastName());
        List<FriendDTO> friendDTOList = StreamSupport.stream(friendDTOS.spliterator(), false).toList();
        modelFriend.setAll(friendDTOList);
    }

    private void initModelReceivedRequest() {
        Iterable<FriendRequestDTO> requests = service.getUsersRequests(loggedUser.getFirstName(),loggedUser.getLastName()); //todo
        List<FriendRequestDTO> friendDTOList = StreamSupport.stream(requests.spliterator(), false).toList();
        modelRequest.setAll(friendDTOList);
    }

    private void initModelSentRequest() {
        Iterable<FriendRequestDTO> requests = service.getUsersSentRequests(loggedUser.getFirstName(),loggedUser.getLastName());
        List<FriendRequestDTO> friendDTOList = StreamSupport.stream(requests.spliterator(), false).toList();
        modelSentRequest.setAll(friendDTOList);
    }

    @FXML
    public void setListChat(){
        this.selectedChat = tableChatFriends.getSelectionModel().selectedItemProperty().getValue();
        initModelChat();
    }

    private void initModelChat() {
        listViewConversation.getItems().clear();
        selectedChat = tableChatFriends.getSelectionModel().selectedItemProperty().getValue();
        if(selectedChat != null) {
            List<Long> ids = new ArrayList<>(selectedChat.getUsers());
            modelMessages.addAll(service.getGroupMessages(ids));

            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    listViewConversation.setItems(modelMessages);
                    listViewConversation.setCellFactory(param -> new ListCell<>() {
                        Label lblUserLeft = new Label();
                        Label lblTextLeft = new Label();
                        HBox hBoxLeft = new HBox(lblUserLeft, lblTextLeft);

                        Label lblUserRight = new Label();
                        Label lblTextRight = new Label();
                        HBox hBoxRight = new HBox(lblTextRight, lblUserRight);

                        {
                            hBoxLeft.setAlignment(Pos.CENTER_LEFT);
                            hBoxLeft.setSpacing(5);
                            hBoxRight.setAlignment(Pos.CENTER_RIGHT);
                            hBoxRight.setSpacing(5);
                        }

                        @Override
                        protected void updateItem(Message item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                if (item.getFromUser().getFirstName().equals(loggedUser.getFirstName()) && item.getFromUser().getLastName().equals(loggedUser.getLastName())) {

                                    lblUserRight.setText(":" + item.getFromUser().getFirstName() + " " + item.getFromUser().getLastName());
                                    lblTextRight.setText(item.getMessage());
                                    setGraphic(hBoxRight);
                                } else {
                                    lblUserLeft.setText(item.getFromUser().getFirstName() + " " + item.getFromUser().getLastName() + ":");
                                    lblTextLeft.setText(item.getMessage());
                                    setGraphic(hBoxLeft);
                                }
                            }
                        }

                    });
                }
            });
        }
    }

    public void sendMessage() {
        String messageText = textFieldMessage.getText();
        List<Tuple<String>> toUsers = new ArrayList<>();
        if(selectedChat == null){
            MessageAlert.showErrorMessage(null, "No chat selected from Chat List");
        }
        else{
            if(selectedChat.getUsers().size() == 2){
                User firstUser = service.getUserRepo().findOne(selectedChat.getUsers().get(0));
                User secondUser = service.getUserRepo().findOne(selectedChat.getUsers().get(1));
                List<User> toUser = new ArrayList<>();
                textFieldMessage.clear();
                if(firstUser.getFirstName().equals(loggedUser.getFirstName()) && firstUser.getLastName().equals(loggedUser.getLastName())){
                    toUsers.add(new Tuple<>(secondUser.getFirstName(),secondUser.getLastName()));
                    toUser.add(secondUser);
                }
                else{
                    toUsers.add(new Tuple<>(firstUser.getFirstName(),firstUser.getLastName()));
                    toUser.add(firstUser);
                }
                service.sendMessage(loggedUser.getFirstName(), loggedUser.getLastName(), toUsers, messageText, null);
                modelMessages.add(new Message(loggedUser,toUser,messageText, LocalDateTime.now(),null));
            }
            else{
                List<User> toUser = new ArrayList<>();
                for(Long id:selectedChat.getUsers()){
                    if(!Objects.equals(loggedUser.getId(), id)) {
                        User user = service.getUserRepo().findOne(id);
                        toUsers.add(new Tuple<>(user.getFirstName(), user.getLastName()));
                        toUser.add(user);
                    }
                }
                service.sendMessage(loggedUser.getFirstName(),loggedUser.getLastName(),toUsers,messageText,null);
                textFieldMessage.clear();
                modelMessages.add(new Message(loggedUser,toUser,messageText,LocalDateTime.now(),null));
            }
            service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.MESSAGE, null));
        }
    }

    @FXML
    public void handleFriendsButton(){
        paneFriends.toFront();
    }

    public void showFriendEditDialog() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/edit-friend-request.fxml"));
            AnchorPane root = loader.load();
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
    public void handleAddFriend(){
        showFriendEditDialog();
    }

    @FXML
    public void handleRemoveFriend(){
        FriendDTO selectedFriendDTO = tableFriends.getSelectionModel().selectedItemProperty().getValue();
        if(selectedFriendDTO == null){
            MessageAlert.showErrorMessage(null, "No friend selected!");
        }
        else{
            service.removeFriend(loggedUser.getFirstName(), loggedUser.getLastName(), selectedFriendDTO.getFriend().getFirstName(), selectedFriendDTO.getFriend().getLastName());
            service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.FRIEND_REMOVE, null));
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend has been removed");
        }
    }

    @FXML
    public void handleRequestsButton(){
        paneRequests.toFront();
    }

    private void respondToRequest(User sender, RequestStatus status){
        switch (status){
            case APPROVED -> {
                service.respondFriendRequest(sender.getFirstName(),sender.getLastName(),loggedUser.getFirstName(),loggedUser.getLastName(), "APPROVE");
                service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.REQUEST_APPROVE, null));
            }
            case REJECTED -> {
                service.respondFriendRequest(sender.getFirstName(),sender.getLastName(),loggedUser.getFirstName(),loggedUser.getLastName(), "REJECT");
                service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.REQUEST_REJECT, null));
            }
        }

        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend request updated!");
    }

    @FXML
    public void handleApproveButton(){
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
    public void handleRejectButton(){
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
    public void handleSentReqButton() {
        sentPane.toFront();
        sentPane.setVisible(true);
        receivedPane.setVisible(false);
    }

    @FXML
    public void handleReceivedReqButton() {
        receivedPane.toFront();
        receivedPane.setVisible(true);
        sentPane.setVisible(false);
    }

    @FXML
    public void handleUnsendButton() {
        FriendRequestDTO requestDTO = tableSentRequests.getSelectionModel().selectedItemProperty().getValue();
        if(requestDTO == null){
            MessageAlert.showErrorMessage(null, "No friend request selected!");
        }
        else if(!requestDTO.getStatus().equals(RequestStatus.PENDING)){
            MessageAlert.showErrorMessage(null, "Friend request already responded!");
        }
        else{
            User user = requestDTO.getSender();
            service.removeFriendRequest(loggedUser.getFirstName(), loggedUser.getLastName(), user.getFirstName(), user.getLastName()); //todo
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend request unsent successfully!");
            service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.REQUEST_UNSEND, null));
        }
    }

    @FXML
    public void handleMessagesButton() {
        paneMessages.toFront();
    }

    @FXML
    public void handleLogOut(){
        accountStage.close();
    }
}
