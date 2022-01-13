package com.example.socialnetworkgui;

import com.example.socialnetworkgui.MessageAlert;
import com.example.socialnetworkgui.domain.Chat;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import com.example.socialnetworkgui.utils.events.UserFriendChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class ChatController {
    private UserFriendshipDbService service;
    Stage addchatStage;
    User loggedUser;
    @FXML
    ListView<User> friendsListView;
    @FXML
    TextField chatNameTextField;
    @FXML
    Button addButton;
    @FXML
    private ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        friendsListView.setItems(model);
        friendsListView.setCellFactory(u->new ListCell<User>(){
            @Override
            protected void updateItem(User item,boolean empty){
                super.updateItem(item, empty);
                if(empty){
                    setText(null);
                }
                else{
                    if(!item.getFirstName().equals(loggedUser.getFirstName()) && !item.getLastName().equals(loggedUser.getLastName())){
                        setText(item.getFirstName() + " " + item.getLastName());
                    }
                }
            }
        });
        friendsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void update() {
       initModel();
    }

    private void initModel(){
        Iterable<User> users = service.getUserRepo().findAll();
        List<User> usersAux = StreamSupport.stream(users.spliterator(),false).toList();
        List<User> finalUser = new ArrayList<>();
        usersAux.forEach(user -> {
            if(!user.getFirstName().equals(loggedUser.getFirstName()) && !user.getLastName().equals(loggedUser.getLastName())){
                finalUser.add(user);
            }
        });
        model.setAll(finalUser);
    }

    public void setService(UserFriendshipDbService service, Stage stage, User loggedUser){
        this.service = service;
        this.addchatStage = stage;
        this.loggedUser = loggedUser;
        initModel();
    }

    @FXML
    public void handleAddButton(){
        List<User> toUsers = friendsListView.getSelectionModel().getSelectedItems();
        List<Long> toUsersFinal = new ArrayList<>();
        for(User user:toUsers){
            toUsersFinal.add(user.getId());
        }
        toUsersFinal.add(loggedUser.getId());
        String name = chatNameTextField.getText();
        Chat newChat = new Chat(name);
        newChat.setUsers(toUsersFinal);
        try {
            service.addChat(newChat);
            addchatStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Chat created successfully!");
        }
        catch (IllegalArgumentException | ValidationException e){
            addchatStage.close();
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}
