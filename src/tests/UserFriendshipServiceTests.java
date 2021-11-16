package tests;

import domain.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.Friendship;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import repository.Repository;
import repository.file.FriendshipFile;
import repository.file.UserFile;
import service.UserFriendshipService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class UserFriendshipServiceTests {
    Repository<Long, User> userRepo;
    Repository<Tuple<Long>, Friendship> friendshipRepo;
    UserFriendshipService srv;

    @BeforeEach
    void setUp() {
        userRepo = new UserFile("data/testUsers.in", new UserValidator());
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