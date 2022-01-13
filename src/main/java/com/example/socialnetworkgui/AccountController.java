package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.EditFriendRequestController;
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
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

public class AccountController implements Observer<UserFriendChangeEvent> {
    Page mainPage;
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
    Button eventButton;
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

    //Event
    @FXML
    Pane paneEvents;
    @FXML
    Button subscribeButton;
    @FXML
    Button unsubscribeButton;
    @FXML
    Button participateButton;
    @FXML
    Button unparticipateButton;
    @FXML
    Button addEventButton;
    @FXML
    TableView<Event> eventTable;
    @FXML
    TableColumn<Event,String> nameColumn;
    @FXML
    TableColumn<Event,String> startDateColumn;
    @FXML
    TableColumn<Event,String> endDateColumn;
    @FXML
    TableColumn<Event,String> descriptionColumn;
    @FXML
    TableColumn<Event,String> locationColumn;
    @FXML
    TableColumn<Event,String> categoryColumn;
    @FXML
    TableColumn<Event,String> organizerColumn;
    @FXML
    Button buttonPreviousPage;
    @FXML
    Button buttonNextPage;
    @FXML
    ObservableList<Event> modelEvents = FXCollections.observableArrayList();

    //Chat
    @FXML
    private TableView<Chat> tableChatFriends;
    @FXML
    private TableColumn<Chat,String> chatColumn;
    @FXML
    Button addChatButton;
    @FXML
    private ListView<Message> listViewConversation;
    @FXML
    private TextField textFieldMessage;
    @FXML
    ObservableList<Message> modelMessages = FXCollections.observableArrayList();
    @FXML
    ObservableList<Chat> modelChat = FXCollections.observableArrayList();
    @FXML
    Chat selectedChat;

    public void setUp(Page page, Stage stage, AnchorPane pane){
        mainPage = page;
        labelLoggedUser.setText(mainPage.getFirstName() + " " + mainPage.getLastName());
        service = page.getService();
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
                if(firstUser.getFirstName().equals(mainPage.getFirstName()) && firstUser.getLastName().equals(mainPage.getLastName())){
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
        nameColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getName()));
        startDateColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getStartDate().toString()));
        endDateColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getEndDate().toString()));
        descriptionColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getDescription()));
        locationColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getLocation()));
        categoryColumn.setCellValueFactory(features ->{
            String category = features.getValue().getCategory();
            SimpleStringProperty categorySting = new SimpleStringProperty(category);
            User organizer = features.getValue().getOrganizer();
            if(organizer.getFirstName().matches(mainPage.getFirstName()) && organizer.getLastName().matches(mainPage.getLastName())){
                return Bindings.when(categorySting.isEmpty())
                        .then(categorySting)
                        .otherwise(categorySting);
            }
            else
            {
                Participant participant = service.getParticipantRepo().findOne(new Tuple<>(features.getValue().getId(),mainPage.getUser().getId()));
                if(participant==null) {
                    categorySting = new SimpleStringProperty("No Participating");
                    return Bindings.when(categorySting.isEmpty())
                            .then(categorySting)
                            .otherwise(categorySting);
                }
                else{
                    categorySting = new SimpleStringProperty("Participating");
                    return Bindings.when(categorySting.isEmpty())
                            .then(categorySting)
                            .otherwise(categorySting);
                }
            }
        });
        organizerColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getOrganizer().getFirstName() + " " + cellData.getValue().getOrganizer().getLastName()));
        eventTable.setItems(modelEvents);
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
            case REGISTRATION: {

            }
            case EVENTS: {
                initModelEvents(service.getEventsOnPage(0));
            }
            case CHAT: {
                initModelChatsList();
            }
            case ALL: {
                initModelChatsList();
                initModelChat();
                initModelFriend();
                initModelReceivedRequest();
                initModelSentRequest();
                initModelEvents(service.getEventsOnPage(0));
            }
        }
    }

    private void sendNotifications(List<Event> eventsList){
        //File imageFile = new File("Images/happy_clown_transparent.png");
        //Image image = new Image(imageFile.toURI().toString());
        eventsList.stream()
                .filter(event -> service.getParticipantRepo().findOne(new Tuple<>(event.getId(), mainPage.getUser().getId())) != null || (event.getOrganizer().getFirstName().equals(mainPage.getFirstName()) && event.getOrganizer().getLastName().equals(mainPage.getLastName()))).forEach(event -> {
                    long days = ChronoUnit.DAYS.between(LocalDateTime.now(), event.getStartDate());
                    String daysLeft = String.valueOf(days);
                    Notifications notificationsBuilder = Notifications.create()
                            //.graphic(new ImageView(image))
                            .title("New upcoming event!")
                            .text(event.getName() + " it's happening in " + daysLeft + " days!")
                            .hideAfter(Duration.seconds(20))
                            .position(Pos.BOTTOM_RIGHT);
                    notificationsBuilder.darkStyle();
                    notificationsBuilder.show();
                });
    }

    private void initModelEvents(Iterable<Event> events){
        List<Event> eventsList = StreamSupport.stream(events.spliterator(),false).toList();
        modelEvents.setAll(eventsList);
        sendNotifications(eventsList);
    }

    public void handlePreviousPageButton() {
        Set<Event> events = service.getPreviousEvents();
        if(events != null && !events.isEmpty()){
            initModelEvents(events);
        }
    }

    public void handleNextPageButton() {
        Set<Event> events = service.getNextEvents();
        if(events != null && !events.isEmpty()){
            initModelEvents(events);
        }
    }

    private void initModelChatsList(){
        Iterable<Chat> chats = service.getAllChats();
        List<Chat> chatsList = StreamSupport.stream(chats.spliterator(),false).toList();
        List<Chat> finalChats = new ArrayList<>();
        chatsList.forEach(chat -> {
            if(chat.getUsers().contains(mainPage.getUser().getId())){
                finalChats.add(chat);
            }
        });
        modelChat.setAll(finalChats);
    }

    private void initModelFriend() {
        Iterable<FriendDTO> friendDTOS = mainPage.getFriendsList();
        List<FriendDTO> friendDTOList = StreamSupport.stream(friendDTOS.spliterator(), false).toList();
        modelFriend.setAll(friendDTOList);
    }

    private void initModelReceivedRequest() {
        Iterable<FriendRequestDTO> requests = mainPage.findReceivedRequests();
        List<FriendRequestDTO> friendDTOList = StreamSupport.stream(requests.spliterator(), false).toList();
        modelRequest.setAll(friendDTOList);
    }

    private void initModelSentRequest() {
        Iterable<FriendRequestDTO> requests = mainPage.findSentRequests();
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
                                if (item.getFromUser().getFirstName().equals(mainPage.getFirstName()) && item.getFromUser().getLastName().equals(mainPage.getLastName())) {

                                    if(item.getReply()!=null){
                                        lblUserRight.setText(":" + item.getFromUser().getFirstName() + " " + item.getFromUser().getLastName());
                                        lblTextRight.setText(item.getReply().getMessage() + '\n'+ item.getMessage());
                                        setGraphic(hBoxRight);
                                    }
                                    else {
                                        lblUserRight.setText(":" + item.getFromUser().getFirstName() + " " + item.getFromUser().getLastName());
                                        lblTextRight.setText(item.getMessage());
                                        setGraphic(hBoxRight);
                                    }
                                } else {
                                    if (item.getReply() != null) {
                                        lblUserLeft.setText(item.getFromUser().getFirstName() + " " + item.getFromUser().getLastName() + ":");
                                        lblTextLeft.setText(item.getReply().getMessage()+ '\n'+ item.getMessage());
                                        setGraphic(hBoxLeft);
                                    } else {
                                        lblUserLeft.setText(item.getFromUser().getFirstName() + " " + item.getFromUser().getLastName() + ":");
                                        lblTextLeft.setText(item.getMessage());
                                        setGraphic(hBoxLeft);
                                    }
                                }
                            }
                        }

                    });
                }
            });
        }
    }

    private List<Tuple<String>> getToUserNames(User firstUser, User secondUser){
        List<Tuple<String>> toUserNames = new ArrayList<>();
        if(firstUser.getFirstName().equals(mainPage.getFirstName()) && firstUser.getLastName().equals(mainPage.getLastName())) {
            toUserNames.add(new Tuple<>(secondUser.getFirstName(), secondUser.getLastName()));
        }
        else{
            toUserNames.add(new Tuple<>(firstUser.getFirstName(), firstUser.getLastName()));
        }
        return toUserNames;
    }

    private List<Tuple<String>> getToUserNames(List<Long> usersIDs){
        List<Tuple<String>> toUserNames = new ArrayList<>();
        for (Long id : usersIDs) {
            if (!Objects.equals(mainPage.getUser().getId(), id)) {
                User user = service.getUserRepo().findOne(id);
                toUserNames.add(new Tuple<>(user.getFirstName(), user.getLastName()));
            }
        }
        return toUserNames;
    }

    private List<User> getToUserList(User firstUser, User secondUser){
        List<User> toUserList = new ArrayList<>();
        if(firstUser.getFirstName().equals(mainPage.getFirstName()) && firstUser.getLastName().equals(mainPage.getLastName())) {
            toUserList.add(secondUser);
        }else {
            toUserList.add(firstUser);
        }
        return toUserList;
    }

    private List<User> getToUserList(List<Long> usersIDs){
        List<User> toUserList = new ArrayList<>();
        for (Long id : usersIDs) {
            if (!Objects.equals(mainPage.getUser().getId(), id)) {
                User user = service.getUserRepo().findOne(id);
                toUserList.add(user);
            }
        }
        return toUserList;
    }

    private void sendMessage(Message selectedMessage, String messageText, List<Tuple<String>> toUserNames, List<User> toUserList) {
        if(selectedMessage == null){
            service.sendMessage(mainPage.getFirstName(), mainPage.getLastName(), toUserNames, messageText, null);
            modelMessages.add(new Message(mainPage.getUser(), toUserList, messageText, LocalDateTime.now(), null));
        }
        else{
            service.sendMessage(mainPage.getFirstName(), mainPage.getLastName(), toUserNames, messageText, selectedMessage);
            modelMessages.add(new Message(mainPage.getUser(),toUserList,messageText, LocalDateTime.now(),selectedMessage));
        }
    }

    @FXML
    public void handleSendButton() {
        Message selectedMessage = listViewConversation.getSelectionModel().getSelectedItem();
        String messageText = textFieldMessage.getText();
        if(selectedChat.getUsers().size() == 2){
            User firstUser = service.getUserRepo().findOne(selectedChat.getUsers().get(0));
            User secondUser = service.getUserRepo().findOne(selectedChat.getUsers().get(1));
            textFieldMessage.clear();
            List<Tuple<String>> toUserNames = getToUserNames(firstUser,secondUser);
            List<User> toUserList = getToUserList(firstUser,secondUser);
            sendMessage(selectedMessage, messageText, toUserNames, toUserList);
        }
        else{
            List<Tuple<String>> toUserNames = getToUserNames(selectedChat.getUsers());
            List<User> toUserList = getToUserList(selectedChat.getUsers());
            sendMessage(selectedMessage, messageText, toUserNames, toUserList);
            textFieldMessage.clear();
        }
        service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.MESSAGE, null));
        listViewConversation.getSelectionModel().clearSelection();
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
            editFriendRequestController.setService(service, dialogStage, mainPage.getUser());
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
            mainPage.removeFriend(selectedFriendDTO.getFriend().getFirstName(), selectedFriendDTO.getFriend().getLastName());
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
                service.respondFriendRequest(sender.getFirstName(),sender.getLastName(),mainPage.getFirstName(),mainPage.getLastName(), "APPROVE");
                service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.REQUEST_APPROVE, null));
            }
            case REJECTED -> {
                service.respondFriendRequest(sender.getFirstName(),sender.getLastName(),mainPage.getFirstName(),mainPage.getLastName(), "REJECT");
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
            mainPage.unsendFriendRequest(user.getFirstName(),user.getLastName());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend request unsent successfully!");
            service.notifyObservers(new UserFriendChangeEvent(ChangeEventType.REQUEST_UNSEND, null));
        }
    }

    @FXML
    public void handleNotificationOnButton(){
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if(selectedEvent == null){
            MessageAlert.showErrorMessage(null, "No event has been selected");
        }
        else {
            if (selectedEvent.getOrganizer().getFirstName().matches(mainPage.getFirstName()) && selectedEvent.getOrganizer().getLastName().matches(mainPage.getLastName())) {
                MessageAlert.showErrorMessage(null, "You're the host, you can't turn on the notifications!");
            } else {
                service.notificationsOn(selectedEvent, mainPage.getUser());
                eventTable.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    public void handleNotificationOffButton(){
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if(selectedEvent == null){
            MessageAlert.showErrorMessage(null, "No event has been selected");
        }
        else {
            if (selectedEvent.getOrganizer().getFirstName().matches(mainPage.getFirstName()) && selectedEvent.getOrganizer().getLastName().matches(mainPage.getLastName())) {
                MessageAlert.showErrorMessage(null, "You're the host, you can't turn off the notifications!");
            } else {
                service.notificationsOff(selectedEvent, mainPage.getUser());
                eventTable.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    public void handleParticipateButton(){
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if(selectedEvent == null){
            MessageAlert.showErrorMessage(null, "No event has been selected!");
        }
        else {
            if (selectedEvent.getOrganizer().getFirstName().matches(mainPage.getFirstName()) && selectedEvent.getOrganizer().getLastName().matches(mainPage.getLastName())) {
                MessageAlert.showErrorMessage(null, "You're the host of the event, you can't do that!");
            } else {
                service.addParticipant(selectedEvent, mainPage.getUser());
                eventTable.getSelectionModel().clearSelection();
            }
            eventTable.getItems().clear();
            initModelEvents(service.getEventsOnPage(0));
        }
    }

    @FXML
    public void handleUnparticipateButton(){
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if(selectedEvent == null){
            MessageAlert.showErrorMessage(null, "No event has been selected");
        }
        else {
            if (selectedEvent.getOrganizer().getFirstName().matches(mainPage.getFirstName()) && selectedEvent.getOrganizer().getLastName().matches(mainPage.getLastName())) {
                MessageAlert.showErrorMessage(null, "You're the host of the event, you can't do that!");
            } else {
                service.removeParticipant(selectedEvent, mainPage.getUser());
                eventTable.getSelectionModel().clearSelection();
            }
            eventTable.getItems().clear();
            initModelEvents(service.getEventsOnPage(0));
        }
    }

    @FXML
    public void handleAddEventButton(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/event.fxml"));
            AnchorPane root = loader.load();
            Stage addEventStage = new Stage();
            addEventStage.setTitle("Add new event.");
            addEventStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            addEventStage.setScene(scene);
            EventController eventController = loader.getController();
            eventController.setService(service, addEventStage,mainPage.getUser());
            addEventStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddChatButton(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/chat.fxml"));
            AnchorPane root = loader.load();
            Stage addchatStage = new Stage();
            addchatStage.setTitle("Add new chat.");
            addchatStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            addchatStage.setScene(scene);
            ChatController ChatController = loader.getController();
            ChatController.setService(service, addchatStage,mainPage.getUser());
            addchatStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleMessagesButton() {
        paneMessages.toFront();
    }

    @FXML
    public void handleEventsButton(){
        paneEvents.toFront();
    }

    @FXML
    public void handleLogOut(){
        accountStage.close();
    }
}
