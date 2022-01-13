package com.example.socialnetworkgui;

import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.domain.validators.FriendshipValidator;
import com.example.socialnetworkgui.domain.validators.MessageValidator;
import com.example.socialnetworkgui.domain.validators.RequestValidator;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.db.*;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import com.example.socialnetworkgui.ui.ConsoleInterface;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        //Repository<Long, User> userRepo = new UserFile("data/user.in", new UserValidator());
        //Repository<Tuple<Long>, Friendship> friendshipRepo = new FriendshipFile("data/friendship.in", new FriendshipValidator());
        //UserFriendshipService srv = new UserFriendshipService(userRepo, friendshipRepo);

        Repository<Long, User> userRepo = new UserDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617",
                new UserValidator());
        Repository<Tuple<Long>, Friendship> friendshipRepo = new FriendshipDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617",
                new FriendshipValidator());
        Repository<Tuple<Long>, FriendRequest> requestRepo = new RequestDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617",
                new RequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617",
                new MessageValidator(),
                userRepo
        );
        Repository<Long,Chat> chatRepository = new ChatDBRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617"
        );
        Repository<Long,Event> eventRepository = new EventDBRepository(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617",
                (UserDbRepo) userRepo
        );
        Repository<Tuple<Long>,Participant> participantRepository = new ParticipantDBRepository(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "834617"
        );


       //UserFriendshipDbService srv = new UserFriendshipDbService(userRepo, friendshipRepo, requestRepo, messageRepo,chatRepository,eventRepository,participantRepository);

        //ConsoleInterface ui = new ConsoleInterface(srv);
        //ui.run();
    }
}
