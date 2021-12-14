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
        loader.setLocation(getClass().getResource("login.fxml"));
        BorderPane root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setUserFriendshipService(service);
        Scene scene = new Scene(root, 520, 400);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Repository<Long, User> userRepo = new UserDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "Pikamar77",
                new UserValidator());
        Repository<Tuple<Long>, Friendship> friendshipRepo = new FriendshipDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "Pikamar77",
                new FriendshipValidator());
        Repository<Tuple<Long>, FriendRequest> requestRepo = new RequestDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "Pikamar77",
                new RequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "Pikamar77",
                new MessageValidator(),
                userRepo
        );
        service = new UserFriendshipDbService(userRepo, friendshipRepo, requestRepo, messageRepo);

        launch();
    }
}
