package com.example.socialnetworkgui.tests;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.FriendshipValidator;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.file.FriendshipFile;
import com.example.socialnetworkgui.repository.file.UserFile;
import com.example.socialnetworkgui.repository.paging.PagingRepository;
import com.example.socialnetworkgui.service.UserFriendshipService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class UserFriendshipServiceTests {
    PagingRepository<Long, User> userRepo;
    Repository<Tuple<Long>, Friendship> friendshipRepo;
    UserFriendshipService srv;

    @BeforeEach
    void setUp() {
        userRepo = (PagingRepository<Long, User>) new UserFile("data/testUsers.in", new UserValidator());
        friendshipRepo = new FriendshipFile("data/testFriendships.in", new FriendshipValidator());
        srv = new UserFriendshipService(userRepo,friendshipRepo);
    }

    @Test
    void addUser() {
        List<User> userList;
        userList = (List<User>) srv.getUsers();
        assert (userList.isEmpty());
        srv.addUser("bogdan","bentea");
        userList = (List<User>) srv.getUsers();
        assert (userList.size() == 1);
        assert (userList.get(0).getFirstName().equals("bogdan"));
        assert (userList.get(0).getLastName().equals("bentea"));
    }

    @Test
    void removeUser() {
        srv.addUser("bogdan","bentea");
        List<User> userList = (List<User>) srv.getUsers();
        assert (!userList.isEmpty());
        srv.removeUser("bogdan","bentea");
        userList = (List<User>) srv.getUsers();
        assert (userList.isEmpty());
    }

    @AfterEach
    void tearDown() {
        try (BufferedWriter clearer = new BufferedWriter(new FileWriter("data/testUsers.in", false))){
            clearer.write("");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        try (BufferedWriter clearer = new BufferedWriter(new FileWriter("data/testFriendships.in", false))){
            clearer.write("");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}