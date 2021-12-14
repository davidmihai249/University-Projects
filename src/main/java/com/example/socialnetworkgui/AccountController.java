package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.EditFriendRequestController;
import com.example.socialnetworkgui.controller.MessageAlert;
import com.example.socialnetworkgui.domain.FriendDTO;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class AccountController implements Observer<UserFriendChangeEvent> {
    User loggedUser;
    UserFriendshipDbService service;
    ObservableList<FriendDTO> model = FXCollections.observableArrayList();
    Stage accountStage;

    @FXML
    TableView<FriendDTO> tableView;
    @FXML
    TableColumn<FriendDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnLastName;
    @FXML
    TableColumn<FriendDTO, LocalDate> tableColumnSince;
    @FXML
    Button buttonAdd;
    @FXML
    Button buttonLogOut;
    @FXML
    Button buttonRemove;
    @FXML
    Button buttonRequests;
    @FXML
    RowConstraints gridPaneTable;

    public void setUserFriendshipService(UserFriendshipDbService userFriendshipDbService, User user, Stage stage){
        loggedUser = user;
        service = userFriendshipDbService;
        service.addObserver(this);
        accountStage = stage;
        initModel();
    }

    public void initialize(){
        tableColumnFirstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFriend().getFirstName()));
        tableColumnLastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFriend().getLastName()));
        tableColumnSince.setCellValueFactory(new PropertyValueFactory<FriendDTO, LocalDate>("date"));
        tableView.setItems(model);
    }

    @Override
    public void update(UserFriendChangeEvent userFriendChangeEvent) {
        initModel();
    }

    private void initModel() {
        Iterable<FriendDTO> friendDTOS = service.getAllFriendships(loggedUser.getFirstName(),loggedUser.getLastName());
        List<FriendDTO> friendDTOList = StreamSupport.stream(friendDTOS.spliterator(), false).toList();
        model.setAll(friendDTOList);
    }

    @FXML
    public void handleAddFriend(ActionEvent ev){
        showFriendEditDialog();
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
    public void handleLogOut(ActionEvent ev){
        accountStage.close();
    }

    @FXML
    public void handleRemoveFriend(ActionEvent ev){
        FriendDTO selectedFriendDTO = tableView.getSelectionModel().selectedItemProperty().getValue();
        if(selectedFriendDTO == null){
            MessageAlert.showErrorMessage(null, "No friend selected!");
        }
        else{
            service.removeFriend(loggedUser.getFirstName(), loggedUser.getLastName(), selectedFriendDTO.getFriend().getFirstName(), selectedFriendDTO.getFriend().getLastName());
            initModel();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend has been removed");
        }
    }
}
