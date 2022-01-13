package com.example.socialnetworkgui;

import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.domain.validators.FriendshipValidator;
import com.example.socialnetworkgui.domain.validators.MessageValidator;
import com.example.socialnetworkgui.domain.validators.RequestValidator;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.db.*;
import com.example.socialnetworkgui.repository.paging.PagingRepository;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class StartApplication extends Application {
    private static UserFriendshipDbService service;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/login.fxml"));
        BorderPane root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setUserFriendshipService(service);
        Scene scene = new Scene(root, 520, 400);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetworkgui.url");
        //String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetworkgui.username");
        //String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetworkgui.password");

        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        String username = "postgres";
        String password = "834617";

        UserDbRepo userRepo = new UserDbRepo(url, username, password, new UserValidator());
        Repository<Tuple<Long>, Friendship> friendshipRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator());
        Repository<Tuple<Long>, FriendRequest> requestRepo = new RequestDbRepo(url, username, password, new RequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepo(url, username, password, new MessageValidator(), userRepo);
        Repository<Long,Chat> chatRepo = new ChatDBRepo(url,username,password);
        PagingRepository<Long,Event> eventRepo = new EventDBRepository(url,username,password, userRepo);
        Repository<Tuple<Long>,Participant> participantRepo = new ParticipantDBRepository(url,username,password);
        service = new UserFriendshipDbService(userRepo, friendshipRepo, requestRepo, messageRepo,chatRepo,eventRepo,participantRepo);

        launch();
    }
}
