package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<Long> friends;

    /**
     * Constructor
     * @param firstName String - user's first name
     * @param lastName String - user's last name
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = new ArrayList<>();
    }

    /**
     * @return a String, user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Modify the first name of the user
     * @param firstName String, user's new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return a String, user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Modify user's last name
     * @param lastName - String, user's new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return a list with all friends of this user
     */
    public List<Long> getFriends() {
        return friends;
    }

    /**
     * @param friends the new list of friends id to modify the current user's friends list
     */
    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}