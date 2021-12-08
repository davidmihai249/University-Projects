package com.example.socialnetworkgui.service;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.FriendshipException;
import com.example.socialnetworkgui.repository.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Repository<Long, User> userRepo;

    /**
     * Constructor
     * @param userRepo the user repository
     */
    public UserService(Repository<Long, User> userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * @return the user repository
     */
    protected Repository<Long, User> getUserRepo() {
        return userRepo;
    }

    /**
     * @param firstName first name of the user wanted to be added
     * @param lastName last name of the user wanted to be added
     */
    public void addUser(String firstName, String lastName){
        User entity = new User(firstName, lastName);
        entity.setId(getNewUserId());
        userRepo.save(entity);
    }

    /**
     * @param firstName first name of the user wanted to be removed
     * @param lastName last name of the user wanted to be removed
     * @return the deleted user
     */
    public User removeUser(String firstName, String lastName){
        User deletedUser = userRepo.delete(getUserID(firstName,lastName));
        removeFromFriendsLists(deletedUser);
        return deletedUser;
    }

    /**
     * @return all the existing users
     */
    public Iterable<User> getUsers(){
        return userRepo.findAll();
    }

    /**
     * Given the name of a user, get its id
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @return the id of the user or null if the user doesn't exist
     */
    public @Nullable Long getUserID(String firstName, String lastName){
        if(firstName == null || lastName == null || firstName.equals("") || lastName.equals("")){
            throw new IllegalArgumentException("\nInvalid user name!");
        }
        Iterable<User> allUsers = userRepo.findAll();
        for (User e : allUsers) {
            if (e.getFirstName().equals(firstName) && e.getLastName().equals(lastName)) {
                return e.getId();
            }
        }
        return null;
    }

    /**
     * Find a user by its firstName and lastName
     * @param firstName of the user
     * @param lastName of the user
     * @return the user if such user exists, or null otherwise
     */
    public User getUser(String firstName, String lastName){
        return userRepo.findOne(getUserID(firstName,lastName));
    }

    /**
     * @param userFirstName user's first name
     * @param userLastName user's last name
     * @return user's friends list
     * @throws FriendshipException if the user does not exist
     */
    public List<User> getFriends(String userFirstName, String userLastName){
        try{
            User user = userRepo.findOne(getUserID(userFirstName,userLastName));
            List<User> friends = new ArrayList<>();
            user.getFriends().forEach(id -> friends.add(userRepo.findOne(id)));
            return friends;
        }
        catch (IllegalArgumentException e){
            throw new FriendshipException("This user does not exist!");
        }
    }

    /**
     * Removes the deleted user from all of its friend "friends list"
     * @param user a deleted user
     */
    private void removeFromFriendsLists(@NotNull User user){
        List<Long> friendsIds = user.getFriends();
        friendsIds.forEach(id -> removeFriendFromList(userRepo.findOne(id), user));
    }

    protected void addFriendInList(@NotNull User user, @NotNull User friend){
        List<Long> updatedFriends = user.getFriends();
        updatedFriends.add(friend.getId());
        user.setFriends(updatedFriends);
    }

    protected void removeFriendFromList(@NotNull User user, @NotNull User friend){
        List<Long> updatedFriends = user.getFriends();
        updatedFriends.remove(friend.getId());
        user.setFriends(updatedFriends);
    }

    /**
     * @return the first available id for a user
     */
    private Long getNewUserId(){
        Long id = 1L;
        boolean ok;
        boolean stop = false;
        while(!stop){
            ok = true;
            for (User u : userRepo.findAll()) {
                if (u.getId().equals(id)) {
                    id++;
                    ok = false;
                    break;
                }
            }
            if(ok){
                stop = true;
            }
        }
        return id;
    }
}
