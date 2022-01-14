package com.example.socialnetworkgui.service;

import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.domain.validators.RequestException;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.domain.validators.ValidatorEvent;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.db.MessageDbRepo;
import com.example.socialnetworkgui.repository.db.RequestDbRepo;
import com.example.socialnetworkgui.repository.paging.Page;
import com.example.socialnetworkgui.repository.paging.Pageable;
import com.example.socialnetworkgui.repository.paging.PageableImplementation;
import com.example.socialnetworkgui.repository.paging.PagingRepository;
import com.example.socialnetworkgui.utils.events.UserFriendChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observable;
import com.example.socialnetworkgui.utils.observer.Observer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserFriendshipDbService extends UserFriendshipService implements Observable<UserFriendChangeEvent> {
    private final Repository<Tuple<Long>, FriendRequest> requestRepo;
    private final Repository<Long, Message> messageRepo;
    private final Repository<Long, Chat> chatRepo;
    private final PagingRepository<Long,Event> eventRepo;
    private final Repository<Tuple<Long>,Participant> participantRepo;
    private List<Observer<UserFriendChangeEvent>> observers = new ArrayList<>();

    public UserFriendshipDbService(Repository<Long, User> userRepo, Repository<Tuple<Long>, Friendship> friendshipRepo, Repository<Tuple<Long>, FriendRequest> requestRepository, Repository<Long, Message> messageRepository, Repository<Long, Chat> chatRepo,PagingRepository<Long,Event> eventRepo,Repository<Tuple<Long>,Participant> participantRepo){
        super(userRepo, friendshipRepo);
        requestRepo = requestRepository;
        messageRepo = messageRepository;
        this.chatRepo = chatRepo;
        this.eventRepo = eventRepo;
        this.participantRepo = participantRepo;
    }

    public Repository<Long, User> getUserRepo(){
        return userService.getUserRepo();
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
     * Remove a friend request
     * @param senderFirstName First name of the user who sent the friend request
     * @param senderLastName Last name of the user who sent the friend request
     * @param receiverFirstName First name of the user who received the friend request
     * @param receiverLastName Last name of the user who received the friend request
     * @throws IllegalArgumentException if the name of at least one user is invalid
     * @throws RequestException if the users exist but there is no request from sender to receiver
     */
    public void removeFriendRequest(String senderFirstName, String senderLastName, String receiverFirstName, String receiverLastName) {
        Long senderID = userService.getUserID(senderFirstName,senderLastName);
        if(senderID == null){
            throw new IllegalArgumentException("\nInvalid sender name!");
        }
        Long receiverID = userService.getUserID(receiverFirstName,receiverLastName);
        if(receiverID == null){
            throw new IllegalArgumentException("\nInvalid receiver name");
        }
        FriendRequest deletedRequest = requestRepo.delete(new Tuple<>(senderID, receiverID));
        if(deletedRequest == null) {
            throw new RequestException("The request does not exist!");
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
        Iterable<FriendRequest> requests = ((RequestDbRepo)requestRepo).getUsersReceivedRequests(receiverId);
        requests.forEach(f -> usersRequests.add(new FriendRequestDTO(userService.getUserRepo().findOne(f.getId().getLeft()), f.getStatus(),f.getDate())));
        return usersRequests;
    }

    public Repository<Tuple<Long>, Participant> getParticipantRepo() {
        return participantRepo;
    }

    /**
     * Get all friend requests sent by a user
     * @param senderFirstName First name of the user who sent the friend requests
     * @param senderLastName Last name of the user who sent the friend requests
     * @return a list with FriendRequestDTO objects
     * @throws IllegalArgumentException if the name of the user is invalid
     */
    public List<FriendRequestDTO> getUsersSentRequests(String senderFirstName, String senderLastName){
        Long senderID = userService.getUserID(senderFirstName,senderLastName);
        if(senderID == null){
            throw new IllegalArgumentException("Sender name is invalid!");
        }
        List<FriendRequestDTO> usersRequests = new ArrayList<>();
        Iterable<FriendRequest> iterableRequests = ((RequestDbRepo)requestRepo).getUsersSentRequests(senderID);
        StreamSupport.stream(iterableRequests.spliterator(), false)
                .forEach(f -> usersRequests.add(new FriendRequestDTO(userService.getUserRepo().findOne(f.getId().getRight()), f.getStatus(),f.getDate())));
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
    public void sendMessage(String FirstNameFrom,String LastNameFrom,List<Tuple<String>> toUsersNames,String messageText,Message Reply){
        List<User> toUsers = new ArrayList<>();
        toUsersNames.forEach(t -> toUsers.add(userService.getUser(t.getLeft(),t.getRight())));
        Message message = new Message(userService.getUser(FirstNameFrom,LastNameFrom),toUsers,messageText,LocalDateTime.now(),Reply);
        messageRepo.save(message);
    }

    public List<Message> getGroupMessages(List<Long> ids){
        Iterable<Message> allMessages = messageRepo.findAll();
        Spliterator<Message> spliterator = allMessages.spliterator();
        return StreamSupport.stream(spliterator, false)
                .filter(m->(ids.contains(m.getFromUser().getId())))
                .filter(m->m.getToUser().size()==ids.size()-1)
                .filter(m->m.getToUser().stream().allMatch(x->ids.contains(x.getId())))
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
    }

    public List<Chat> getAllChats(){
        Iterable<Chat> allChats = chatRepo.findAll();
        Spliterator<Chat> spliterator = allChats.spliterator();
        return StreamSupport.stream(spliterator,false).toList();
    }

    public void addChat(Chat newChat){
        chatRepo.save(newChat);
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


    /**
     * Reply to all users
     * @param FirstNameFrom String for the first name of the user who sends the message.
     * @param LastNameFrom String for the last name of the user who sends the message.
     * @param messageID The id of the message to be replied to.
     * @param messageText String for the sent message.
     * @throws IllegalArgumentException if the message id is invalid, or if the user can't reply to that message
     */
    public void replyAll(String FirstNameFrom,String LastNameFrom, Long messageID,String messageText){
        try{
            Message message = messageRepo.findOne(messageID);
            List<User> Users = new ArrayList<>();
            Long senderID = userService.getUserID(FirstNameFrom,LastNameFrom);
            User sender = userService.getUserRepo().findOne(senderID);
            for(Message mess: messageRepo.findAll()){
                if(mess.getId().equals(messageID)){
                    if(mess.getToUser().contains(sender)) {
                        for (User user : mess.getToUser()) {
                            if (!user.getFirstName().equals(FirstNameFrom) && !user.getLastName().equals(LastNameFrom)) {
                                Users.add(user);
                            }
                        }
                        Users.add(mess.getFromUser());
                        Message reply = new Message(sender, Users, messageText, LocalDateTime.now(), message);
                        messageRepo.save(reply);
                    }
                    else{
                        throw new IllegalArgumentException("This user can't reply to that message!");
                    }
                }
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Message id is invalid!");
        }
    }

    /**
     * Find all messages received by a user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @return a list with messages
     */
    public List<Message> getReceivedMessages(String firstName, String lastName) {
        User user = getUser(firstName, lastName); //todo
        return ((MessageDbRepo)messageRepo).getReceivedMessagesIDs(user.getId());
    }

    private int page = 0;
    private final int size = 4;

    public Repository<Long, Event> getEventRepo() {
        return eventRepo;
    }

    public Set<Event> getEventsOnPage(int pageNumber){
        this.page = pageNumber;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Event> eventsPage = eventRepo.findAll(pageable);
        return eventsPage.getContent().collect(Collectors.toSet());
    }

    public Set<Event> getPreviousEvents(){
        this.page--;
        if(page >= 0){
            Set<Event> events = getEventsOnPage(this.page);
            if(events != null && !events.isEmpty()){
                return getEventsOnPage(this.page);
            }
            else{
                this.page++;
                return null;
            }
        }
        this.page++;
        return null;
    }

    public Set<Event> getNextEvents(){
        this.page++;
        Set<Event> events = getEventsOnPage(this.page);
        if(events != null && !events.isEmpty()){
            return events;
        }
        else{
            this.page--;
            return null;
        }
    }

    public int getPage(){
        return this.page;
    }

    public void setPage(int newPage){
        this.page = newPage;
    }

    public void addEvent(Event event) throws ValidationException{
        Validator<Event> validatorEvent = new ValidatorEvent();
        validatorEvent.validate(event);
        eventRepo.save(event);
    }

    public void addParticipant(Event event,User user){
        participantRepo.save(new Participant(event.getId(),user.getId()));
    }

    public void removeParticipant(Event event,User user){
        participantRepo.delete(new Tuple<>(event.getId(),user.getId()));
    }

    public void notificationsOn(Event event, User user){
        participantRepo.update(new Participant(event.getId(),user.getId(),true));
    }

    public void notificationsOff(Event event, User user){
        participantRepo.update(new Participant(event.getId(),user.getId(),false));
    }

    public List<Event> getAllEvents(){
        Iterable<Event> allEvents = eventRepo.findAll();
        Spliterator<Event> spliterator = allEvents.spliterator();
        return StreamSupport.stream(spliterator,false).toList();
    }

    public List<Friendship> getFriendsStatistics(User user, LocalDate startDate,LocalDate endDate){
        Iterable<Friendship> friendships = friendshipService.getFriendshipRepo().findAll();
        Spliterator<Friendship> spliterator = friendships.spliterator();
        return StreamSupport.stream(spliterator, false)
                .filter(m -> m.getId().getLeft().equals(user.getId()) || m.getId().getRight().equals(user.getId()))
                .filter(m -> m.getDate().isAfter(startDate) && m.getDate().isBefore(endDate))
                .sorted(Comparator.comparing(Friendship::getDate))
                .toList();
    }

    public List<Message> getMessageStatistics(User user,LocalDate startDate,LocalDate endDate){
        LocalDateTime time1 = startDate.atStartOfDay();
        LocalDateTime time2 = endDate.atStartOfDay();
        Iterable<Message> messages = messageRepo.findAll();
        Spliterator<Message> spliterator = messages.spliterator();
        return StreamSupport.stream(spliterator,false)
                .filter(m->m.getDate().isAfter(time1) && m.getDate().isBefore(time2))
                .filter(m->m.getToUser().contains(user))
                .toList();
    }

    public List<Message> getMessageStatistics2(User from,User user,LocalDate startDate,LocalDate endDate){
        LocalDateTime time1 = startDate.atStartOfDay();
        LocalDateTime time2 = endDate.atStartOfDay();
        Iterable<Message> messages = messageRepo.findAll();
        Spliterator<Message> spliterator = messages.spliterator();
        return StreamSupport.stream(spliterator,false)
                .filter(m->m.getDate().isAfter(time1) && m.getDate().isBefore(time2))
                .filter(m->m.getToUser().contains(user))
                .filter(m->m.getFromUser().equals(from))
                .toList();
    }

    @Override
    public void addObserver(Observer<UserFriendChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserFriendChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserFriendChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
