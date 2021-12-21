package com.example.socialnetworkgui;

import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.domain.validators.FriendshipValidator;
import com.example.socialnetworkgui.domain.validators.MessageValidator;
import com.example.socialnetworkgui.domain.validators.RequestValidator;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.db.FriendshipDbRepo;
import com.example.socialnetworkgui.repository.db.MessageDbRepo;
import com.example.socialnetworkgui.repository.db.RequestDbRepo;
import com.example.socialnetworkgui.repository.db.UserDbRepo;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import com.example.socialnetworkgui.config.ApplicationContext;
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
        String password = "Pikamar77";

        Repository<Long, User> userRepo = new UserDbRepo(url, username, password, new UserValidator());
        Repository<Tuple<Long>, Friendship> friendshipRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator());
        Repository<Tuple<Long>, FriendRequest> requestRepo = new RequestDbRepo(url, username, password, new RequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepo(url, username, password, new MessageValidator(), userRepo);
        service = new UserFriendshipDbService(userRepo, friendshipRepo, requestRepo, messageRepo);

        launch();
    }
}
