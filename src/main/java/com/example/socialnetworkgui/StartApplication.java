package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.LoginController;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.db.UserDbRepo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class StartApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("login.fxml"));
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        BorderPane root = loader.load();
        UserDbRepo userRepo = new UserDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617",
                new UserValidator());
        LoginController loginController = loader.getController();
        loginController.setUserDbRepo(userRepo);
        Scene scene = new Scene(root, 520, 400);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
