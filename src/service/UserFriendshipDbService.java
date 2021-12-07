package service;

import domain.*;
import domain.validators.RequestException;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class UserFriendshipDbService extends UserFriendshipService{
    private final Repository<Tuple<Long>, FriendRequest> requestRepo;
    private final Repository<Long, Message> messageRepo;

    public UserFriendshipDbService(Repository<Long, User> userRepo, Repository<Tuple<Long>, Friendship> friendshipRepo, Repository<Tuple<Long>, FriendRequest> requestRepository, Repository<Long, Message> messageRepository) {
        super(userRepo, friendshipRepo);
        requestRepo = requestRepository;
        messageRepo = messageRepository;
    }

    @Override
    public List<User> getFriends(String userFirstName, String userLastName){
        Long userId = userService.getUserID(userFirstName,userLastName);
        List<User> friends = new ArrayList<>();
        userService
                .getUserRepo()
                .findOne(userId)
                .getFriends()
                .forEach(f -> friends.add(userService.getUserRepo().findOne(f)));
        return friends;
    }

    @Override
    protected @NotNull Map<Long, List<Long>> getUserRelations() {
        Map<Long, List<Long>> userRelationships = new HashMap<>();
        userService.getUserRepo().findAll().forEach(u ->
                userRelationships.put(u.getId(), getIdsFromUsers(getFriends(u.getFirstName(),u.getLastName()))));
        return userRelationships;
    }

    private @NotNull List<Long> getIdsFromUsers(@NotNull List<User> users){
        List<Long> userIds = new ArrayList<>();
        users.forEach(u -> userIds.add(u.getId()));
        return userIds;
    }

    /**
     * Add a new friend request
     * @param senderFirstName First name of the user who sends the friend request
     * @param senderLastName Last name of the user who sends the friend request
     * @param receiverFirstName First name of the user who receives the friend request
     * @param receiverLastName Last name of the user who receives the friend request
     * @throws IllegalArgumentException if the name of at least one user is invalid
     */
    public void addFriendRequest(String senderFirstName, String senderLastName, String receiverFirstName, String receiverLastName) {
        Long senderID = userService.getUserID(senderFirstName,senderLastName);
        if(senderID == null){
            throw new IllegalArgumentException("\nInvalid sender name!");
        }
        Long receiverID = userService.getUserID(receiverFirstName,receiverLastName);
        if(receiverID == null){
            throw new IllegalArgumentException("\nInvalid receiver name");
        }
        FriendRequest request = new FriendRequest(senderID, receiverID, RequestStatus.PENDING);
        if(requestRepo.findOne(request.getId()) == null && requestRepo.findOne(new Tuple<>(receiverID,senderID)) == null){
            requestRepo.save(request);
        }
        else{
            if(friendshipService.getFriendshipRepo().findOne(request.getId()) != null || friendshipService.getFriendshipRepo().findOne(new Tuple<>(receiverID,senderID)) != null){
                throw new RequestException("These users are already friends!");
            }
            else{
                throw new RequestException("There already exists a friend request between the two users!");
            }
        }
    }

    /**
     * Answer a friend request by changing its status
     * @param senderFirstName First name of the user who sent the friend request
     * @param senderLastName Last name of the user who sent the friend request
     * @param receiverFirstName First name of the user who received the friend request
     * @param receiverLastName Last name of the user who received the friend request
     * @param status The new status of the friend request
     * @throws IllegalArgumentException if the name of at least one user is invalid or if the status is invalid
     */
    public void respondFriendRequest(String senderFirstName, String senderLastName, String receiverFirstName, String receiverLastName, String status) {
        Long senderID = userService.getUserID(senderFirstName,senderLastName);
        if(senderID == null){
            throw new IllegalArgumentException("\nInvalid sender name!");
        }
        Long receiverID = userService.getUserID(receiverFirstName,receiverLastName);
        if(receiverID == null){
            throw new IllegalArgumentException("\nInvalid receiver name");
        }
        if(!status.equals("APPROVE") && !status.equals("REJECT")){
            throw new IllegalArgumentException("\nInvalid status! (Status should be APPROVED or REJECTED)!");
        }
        if(status.equals("APPROVE")){
            FriendRequest request = new FriendRequest(senderID, receiverID, RequestStatus.APPROVED);
            FriendRequest resultRequest = requestRepo.update(request);
            if(resultRequest != null){
                throw new RequestException("There is no friend request between this two users!");
            }
            super.addFriend(senderFirstName,senderLastName,receiverFirstName,receiverLastName);
        }
        else{
            FriendRequest request = new FriendRequest(senderID, receiverID, RequestStatus.REJECTED);
            FriendRequest resultRequest = requestRepo.update(request);
            if(resultRequest != null){
                throw new RequestException("There is no friend request between this two users!");
            }
            super.removeFriend(senderFirstName,senderLastName,receiverFirstName,receiverLastName);
        }
    }

    /**
     * Get all friend requests of a user
     * @param receiverFirstName First name of the user who received the friend requests
     * @param receiverLastName Last name of the user who received the friend requests
     * @return a list with FriendRequestDTO objects
     * @throws IllegalArgumentException if the name of the user is invalid
     */
    public List<FriendRequestDTO> getUsersRequests(String receiverFirstName, String receiverLastName){
        Long receiverId = userService.getUserID(receiverFirstName,receiverLastName);
        if(receiverId == null){
            throw new IllegalArgumentException("Receiver name is invalid!");
        }
        List<FriendRequestDTO> usersRequests = new ArrayList<>();
        Iterable<FriendRequest> allRequests = requestRepo.findAll();
        Spliterator<FriendRequest> spliterator = allRequests.spliterator();
        StreamSupport.stream(spliterator, false)
                .filter(x -> x.getId().getRight().equals(receiverId))
                .forEach(f -> usersRequests.add(new FriendRequestDTO(userService.getUserRepo().findOne(f.getId().getLeft()), f.getStatus())));
        return usersRequests;
    }

    /**
     * Get the full conversation between two users
     * @param firstUserNames tuple - containing the first and last name of first user
     * @param secondUserNames tuple - containing the first and last name of second user
     * @return a List with objects of type Message
     */
    public List<Message> getFullConversation(Tuple<String> firstUserNames, Tuple<String> secondUserNames){
        User firstUser = userService.getUser(firstUserNames.getLeft(),firstUserNames.getRight());
        User secondUser = userService.getUser(secondUserNames.getLeft(),secondUserNames.getRight());
        Iterable<Message> allMessages = messageRepo.findAll();
        Spliterator<Message> spliterator = allMessages.spliterator();
        return StreamSupport.stream(spliterator, false)
                .filter(m -> m.getFromUser().equals(firstUser) && m.getToUser().contains(secondUser)
                          || m.getFromUser().equals(secondUser) && m.getToUser().contains(firstUser))
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
    }

    /**
     * Sending a message to a user or a group of users.
     * @param FirstNameFrom String for the first name of the user who sends the message.
     * @param LastNameFrom String for the last name of the user who sends the message.
     * @param toUsersNames List of tuple containing the first and last name of the users who receive the message.
     * @param messageText String for the sent message.
     */
    public void sendMessage(String FirstNameFrom,String LastNameFrom,List<Tuple<String>> toUsersNames,String messageText){
        List<User> toUsers = new ArrayList<>();
        toUsersNames.forEach(t -> toUsers.add(userService.getUser(t.getLeft(),t.getRight())));
        Message message = new Message(userService.getUser(FirstNameFrom,LastNameFrom),toUsers,messageText,LocalDateTime.now(),null);
        messageRepo.save(message);
    }

    /**
     * Reply to a message
     * @param FirstNameFrom String for the first name of the user who sends the message.
     * @param LastNameFrom String for the last name of the user who sends the message.
     * @param messageID The id of the message to be replied to.
     * @param messageText String for the sent message.
     */
    public void replyMessage(String FirstNameFrom,String LastNameFrom, Long messageID, String messageText){
        try{
            Message message = messageRepo.findOne(messageID);
            User fromUser = message.getFromUser();
            List<User> toUsers = message.getToUser();
            Long senderID = userService.getUserID(FirstNameFrom,LastNameFrom);
            User sender = userService.getUserRepo().findOne(senderID);
            if(toUsers.contains(sender)){
                List<User> toUser = new ArrayList<>();
                toUser.add(fromUser);
                Message reply = new Message(sender,toUser,messageText,LocalDateTime.now(),message);
                messageRepo.save(reply);
            }
            else{
                throw new IllegalArgumentException("This user can't reply to that message!");
            }
        }
        catch (NullPointerException e){
            throw new IllegalArgumentException("Message id is invalid!");
        }
    }
}
