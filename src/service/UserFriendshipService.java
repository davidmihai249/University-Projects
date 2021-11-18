package service;

import domain.FriendDTO;
import domain.Tuple;
import org.jetbrains.annotations.NotNull;
import domain.Friendship;
import domain.User;
import domain.validators.FriendshipException;
import repository.Repository;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class UserFriendshipService {
    protected final UserService userService;
    protected final FriendshipService friendshipService;
    protected final GraphUtils graphUtils;

    /**
     * Constructor
     * - initializes both UserService and FriendshipService
     * @param userRepo the users repository
     * @param friendshipRepo the friendships repository
     */
    public UserFriendshipService(Repository<Long, User> userRepo, Repository<Tuple<Long>, Friendship> friendshipRepo) {
        this.userService = new UserService(userRepo);
        this.friendshipService = new FriendshipService(friendshipRepo);
        loadFriendships();
        graphUtils = new GraphUtils();
    }

    /**
     * load all the friendships from the friendship repository
     */
    private void loadFriendships(){
        Iterable<Friendship> allFriendships = friendshipService.getFriendshipRepo().findAll();
        for(Friendship fr : allFriendships){
            User user1 = userService.getUserRepo().findOne(fr.getId().getLeft());
            User user2 = userService.getUserRepo().findOne(fr.getId().getRight());
            userService.addFriendInList(user1, user2);
            userService.addFriendInList(user2, user1);
        }
    }

    /**
     * Add a new user
     * @param firstName first name of the user
     * @param lastName last name of the user
     */
    public void addUser(String firstName, String lastName){
        userService.addUser(firstName,lastName);
    }

    /**
     * Remove a user
     * @param firstName first name of the user wanted to be removed
     * @param lastName last name of the user wanted to be removed
     */
    public void removeUser(String firstName, String lastName){
        try {
            User deletedUser = userService.removeUser(firstName,lastName);
            removeFromFriendships(deletedUser.getId());
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid user!");
        }
    }

    /**
     * Removes all friendships in which the deleted user appears
     * @param deletedUserId the id of the deleted user
     */
    private void removeFromFriendships(Long deletedUserId){
        List<Tuple<Long>> friendshipsId = friendshipService.getFriendships(deletedUserId);
        friendshipsId.forEach(id -> friendshipService.getFriendshipRepo().delete(id));
    }

    /**
     * @return an iterable with all the existing users
     */
    public Iterable<User> getUsers(){
        return userService.getUsers();
    }

    /**
     * Return a user's friend list
     * @param userFirstName user's first name
     * @param userLastName user's last name
     * @return all the friends of the user
     */
    public List<User> getFriends(String userFirstName, String userLastName){
        return userService.getFriends(userFirstName,userLastName);
    }

    /**
     * Add a friend in the user's friends list
     * @param userFirstName user's first name
     * @param userLastName user's last name
     * @param friendFirstName friend's first name
     * @param friendLastName friend's last name
     * @throws FriendshipException if "friend" is already in the user's friend list
     */
    public void addFriend(String userFirstName, String userLastName, String friendFirstName, String friendLastName){
        User user = userService.getUserRepo().findOne(userService.getUserID(userFirstName, userLastName));
        User friend = userService.getUserRepo().findOne(userService.getUserID(friendFirstName, friendLastName));
        List<Long> friends = user.getFriends();
        if(friends.contains(friend.getId())){
            throw new FriendshipException("These users are already friends!");
        }
        userService.addFriendInList(user,friend);
        userService.addFriendInList(friend,user);
        friendshipService.addFriendship(user.getId(), friend.getId(), LocalDate.now());
    }

    /**
     * Remove a friend from the user's friends list
     * @param userFirstName user's first name
     * @param userLastName user's last name
     * @param friendFirstName friend's first name
     * @param friendLastName friend's last name
     * @throws FriendshipException if "friend" is not in the user's friend list
     */
    public void removeFriend(String userFirstName, String userLastName, String friendFirstName, String friendLastName){
        try{
            User user = userService.getUserRepo().findOne(userService.getUserID(userFirstName, userLastName));
            User friend = userService.getUserRepo().findOne(userService.getUserID(friendFirstName, friendLastName));
            List<Long> friends = user.getFriends();
            if(!friends.contains(friend.getId())){
                throw new FriendshipException("These users are not friends!");
            }
            userService.removeFriendFromList(user,friend);
            userService.removeFriendFromList(friend,user);
            friendshipService.removeFriendship(friendshipService.getFriendshipId(user.getId(),friend.getId()));
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("There is an invalid user!");
        }
    }

    /**
     * @return the number of communities
     */
    public Integer getCommunitiesNumber(){
        graphUtils.setConnectedComponents(getUserRelations());
        Integer commNumbers = graphUtils.getConnectedComponents();
        if (commNumbers == ((Collection<User>) userService.getUserRepo().findAll()).size()){
            return 0;
        }
        return commNumbers;
    }

    /**
     * @return a list containing the users from the biggest community
     */
    public List<User> getBiggestCommunity(){
        if(getCommunitiesNumber() == 0){
            return null;
        }
        List<Long> userIds = graphUtils.getBiggestComponent();
        List<User> users = new ArrayList<>();
        userIds.forEach(id -> users.add(userService.getUserRepo().findOne(id)));
        return users;
    }

    /**
     * @return the adjacency list of the friendships graph
     */
    protected @NotNull Map<Long, List<Long>> getUserRelations(){
        Map<Long, List<Long>> userRelationships = new HashMap<>();
        userService.getUserRepo().findAll().forEach(u -> userRelationships.put(u.getId(), u.getFriends()));
        return userRelationships;
    }

    /**
     * Return a list with all the friendships that a user has
     * @param UserFirstName first name of the user
     * @param UserLastName last name of the user
     * @return a list with FriendDTO objects
     */
    public List<FriendDTO> getAllFriendships(String UserFirstName,String UserLastName){
        Long UserID = userService.getUserID(UserFirstName,UserLastName);
        if(UserID == null){
            throw new IllegalArgumentException("\nInvalid user!");
        }
        List<Tuple<Long>> friendshipsIDS = friendshipService.getFriendships(UserID);
        List<Friendship> friendships = new ArrayList<>();
        friendshipsIDS.forEach(t -> friendships.add(friendshipService.getFriendshipRepo().findOne(t)));

        List<FriendDTO> friendDTOS = new ArrayList<>();
        friendships
                .stream()
                .forEach(f -> {
                            User friend = userService.getUserRepo().findOne(f.getId().getLeft());
                            if(friend.getId().equals(UserID)){
                                friend = userService.getUserRepo().findOne(f.getId().getRight());
                            }
                            LocalDate date = f.getDate();
                            friendDTOS.add(new FriendDTO(friend,date));});
        return friendDTOS;
    }
    /**
     * Return a list with all the friends of a user which were made in a certain month
     * @param userFirstName first name of the user
     * @param userLastName last name of the user
     * @param month the month in which the friendship was made
     * @return a list with FriendDTO objects
     */
    public List<FriendDTO> getFriendshipByMonth(String userFirstName, String userLastName, String month){
        if(month == null || month.equals("")){
            throw new IllegalArgumentException("\nInvalid month!");
        }
        Month enumMonth = getMonthByString(month);
        if(enumMonth == null){
            throw new IllegalArgumentException("\nInvalid month (make sure it's written in english with small letters)!");
        }

        Long userId = userService.getUserID(userFirstName,userLastName);
        if (userId == null){
            throw new IllegalArgumentException("\nInvalid user!");
        }
        List<Tuple<Long>> friendshipsIDs = friendshipService.getFriendships(userId);
        List<Friendship> friendships = new ArrayList<>();
        friendshipsIDs.forEach(t -> friendships.add(friendshipService.getFriendshipRepo().findOne(t)));

        List<FriendDTO> friendDTOS = new ArrayList<>();
        friendships
                .stream()
                .filter(f -> f.getDate().getMonth().equals(enumMonth))
                .forEach(f -> {
                    User friend = userService.getUserRepo().findOne(f.getId().getLeft());
                    if(friend.getId().equals(userId)){
                        friend = userService.getUserRepo().findOne(f.getId().getRight());
                    }
                    LocalDate date = f.getDate();
                    friendDTOS.add(new FriendDTO(friend,date));
                });
        return friendDTOS;
    }

    /**
     * @param month given string representing a month
     * @return an enum with the correct month
     */
    private Month getMonthByString(String month){
        return switch (month) {
            case "january" -> Month.JANUARY;
            case "february" -> Month.FEBRUARY;
            case "march" -> Month.MARCH;
            case "april" -> Month.APRIL;
            case "may" -> Month.MAY;
            case "june" -> Month.JUNE;
            case "july" -> Month.JULY;
            case "august" -> Month.AUGUST;
            case "september" -> Month.SEPTEMBER;
            case "october" -> Month.OCTOBER;
            case "november" -> Month.NOVEMBER;
            case "december" -> Month.DECEMBER;
            default -> null;
        };
    }
}