package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.User;

public class UserTests {
    User user1,user2,user3;

    @BeforeEach
    void setUp() {
        user1 = new User("bogdan","bentea");
        user1.setId(9L);
        user2 = new User("alexandru","popa");
        user2.setId(4L);
        user3 = new User("raul","cristescu");
        user3.setId(5L);
    }

    @Test
    void getters(){
        assert (user1.getId().equals(9L));
        assert (user2.getId().equals(4L));
        assert (user3.getId().equals(5L));
        assert (user1.getFirstName().equals("bogdan"));
        assert (user2.getFirstName().equals("alexandru"));
        assert (user3.getFirstName().equals("raul"));
        assert (user1.getLastName().equals("bentea"));
        assert (user2.getLastName().equals("popa"));
        assert (user3.getLastName().equals("cristescu"));
    }

    @Test
    void setters(){
        assert (user1.getId().equals(9L));
        user1.setId(7L);
        assert (user1.getId().equals(7L));
        assert (user1.getFirstName().equals("bogdan"));
        user1.setFirstName("bogdan2");
        assert (user1.getFirstName().equals("bogdan2"));
        assert (user1.getLastName().equals("bentea"));
        user1.setLastName("bentea2");
        assert (user1.getLastName().equals("bentea2"));
    }

    @Test
    void equality() {
        assert (!user1.equals(user2));
        user2.setFirstName(user1.getFirstName());
        user2.setLastName(user1.getLastName());
        user2.setFriends(user1.getFriends());
        assert (user1.equals(user2));
    }
}
