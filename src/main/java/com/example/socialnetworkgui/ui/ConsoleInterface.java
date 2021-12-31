package com.example.socialnetworkgui.ui;

import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.domain.validators.FriendshipException;
import com.example.socialnetworkgui.domain.validators.RequestException;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.service.UserFriendshipDbService;
import org.jetbrains.annotations.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConsoleInterface {
    private final UserFriendshipDbService srv;

    public ConsoleInterface(UserFriendshipDbService srv) {
       this.srv = srv;
    }

    public void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        running:
        while(true){
            Menu.printMenu();
            System.out.print("\nGive command: ");
            String cmd = reader.readLine();
            try{
                switch (cmd) {
                    case "X" -> {
                        System.out.println("\nClosing the app!");
                        break running;
                    }
                    case "1" -> addUser(reader);
                    case "2" -> removeUser(reader);
                    case "3" -> allUsers();
                    case "4" -> addFriendship(reader);
                    case "5" -> removeFriendship(reader);
                    case "6" -> userFriends(reader);
                    case "7" -> communitiesNumber();
                    case "8" -> biggestCommunity();
                    case "9" -> friendsByMonth(reader);
                    case "10" -> getAllFriends(reader);
                    case "11" -> addRequest(reader);
                    case "12" -> allRequestsOfUser(reader);
                    case "13" -> respondToRequest(reader);
                    case "14" -> getConversation(reader);
                    case "15" -> sendMessage(reader);
                    case "16" -> replyMessage(reader);
                    case "17" -> replyAll(reader);
                    default -> System.out.println("Invalid command!");
                }
            }
            catch (ValidationException | IllegalArgumentException | FriendshipException | RequestException | NullPointerException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private String[] readUserAndFriend(@NotNull BufferedReader reader) throws IOException {
        System.out.print("Give user's first name: ");
        String firstName = reader.readLine();
        System.out.print("Give user's last name: ");
        String lastName = reader.readLine();
        System.out.print("Give friend's first name: ");
        String friendFirstName = reader.readLine();
        System.out.print("Give friend's last name: ");
        String friendLastName = reader.readLine();
        return new String[] {firstName,lastName,friendFirstName,friendLastName};
    }

    private String[] readUser(@NotNull BufferedReader reader) throws IOException {
        System.out.print("Give user's first name: ");
        String firstName = reader.readLine();
        System.out.print("Give user's last name: ");
        String lastName = reader.readLine();
        return new String[]{firstName,lastName};
    }

    private void addUser(BufferedReader reader) throws IOException {
        String[] nameList = readUser(reader);
        srv.addUser(nameList[0], nameList[1]);
        System.out.println("User added successfully");
    }

    private void removeUser(BufferedReader reader) throws IOException {
        String[] nameList = readUser(reader);
        srv.removeUser(nameList[0], nameList[1]);
        System.out.println("User removed successfully");
    }

    private void allUsers() {
        Iterable<User> allUsers = srv.getUsers();
        char dot = '\u00B0';
        if ( ((Collection<User>) allUsers).size() == 0){
            System.out.println("There are no registered users!");
        }
        else{
            System.out.println("\nExisting Users: ");
            allUsers.forEach(u -> System.out.println(dot + " " + u.getFirstName() + " " + u.getLastName()));
        }
    }

    private void addFriendship(BufferedReader reader) throws IOException {
        String[] nameList = readUserAndFriend(reader);
        srv.addFriend(nameList[0], nameList[1], nameList[2], nameList[3]);
        System.out.println("Friendship added successfully");
    }

    private void removeFriendship(BufferedReader reader) throws IOException {
        String[] nameList = readUserAndFriend(reader);
        srv.removeFriend(nameList[0], nameList[1], nameList[2], nameList[3]);
        System.out.println("Friendship removed successfully");
    }

    private void userFriends(BufferedReader reader) throws IOException {
        String[] nameList = readUser(reader);
        List<User> friends = srv.getFriends(nameList[0], nameList[1]);
        if (friends.isEmpty()) {
            System.out.println("The user has no friends!");
        }
        else {
            char dot = '\u00B0';
            System.out.println("\nFriends: ");
            friends.stream().map(u -> dot + " " + u.getFirstName() + " " + u.getLastName()).forEach(System.out::println);
        }
    }

    private void communitiesNumber() {
        Integer communitiesNumber = srv.getCommunitiesNumber();
        switch (communitiesNumber) {
            case 0 -> System.out.println("There are no communities!");
            case 1 -> System.out.println("There is 1 community!");
            default -> System.out.println("There are " + communitiesNumber + " communities");
        }
    }

    private void biggestCommunity() {
        List<User> friends = srv.getBiggestCommunity();
        if (friends == null) {
            System.out.println("There are no communities!");
        } else {
            char dot = '\u00B0';
            System.out.println("\nBiggest community: ");
            friends.stream().map(u -> dot + " " + u.getFirstName() + " " + u.getLastName()).forEach(System.out::println);
        }
    }

    private void friendsByMonth(@NotNull BufferedReader reader) throws IOException {
        String[] nameList = readUser(reader);
        System.out.print("Give friendships month (ex: october): ");
        String month = reader.readLine();
        List<FriendDTO> friendDTOS = srv.getFriendshipByMonth(nameList[0], nameList[1], month);
        if(friendDTOS.isEmpty()){
            System.out.println("The user has no friends made in that month!");
        }
        else{
            friendDTOS.forEach(f -> System.out.println(f.getFriend().getFirstName() + " | " + f.getFriend().getLastName() + " | " + f.getDate()));
        }
    }

    private void getAllFriends(@NotNull BufferedReader reader) throws IOException{
        String[] nameList = readUser(reader);
        List<FriendDTO> friendDTOS = srv.getAllFriendships(nameList[0],nameList[1]);
        if(friendDTOS.isEmpty()){
            System.out.println("The user has no friends!");
        }
        else{
            friendDTOS.forEach(f-> System.out.println(f.getFriend().getFirstName() + " | " + f.getFriend().getLastName() + " | " + f.getDate()));
        }
    }

    private String[] readSenderAndReceiver(@NotNull BufferedReader reader) throws IOException {
        System.out.print("Give sender's first name: ");
        String senderFirstName = reader.readLine();
        System.out.print("Give sender's last name: ");
        String senderLastName = reader.readLine();
        System.out.print("Give receiver's first name: ");
        String receiverFirstName = reader.readLine();
        System.out.print("Give receiver's last name: ");
        String receiverLastName = reader.readLine();
        return new String[] {senderFirstName,senderLastName,receiverFirstName,receiverLastName};
    }

    private void addRequest(BufferedReader reader) throws IOException {
        String[] names = readSenderAndReceiver(reader);
        srv.addFriendRequest(names[0],names[1],names[2],names[3]);
        System.out.println("Friend request sent successfully!");
    }

    private void allRequestsOfUser(BufferedReader reader) throws IOException {
        String[] name = readUser(reader);
        List<FriendRequestDTO> requests = srv.getUsersRequests(name[0],name[1]);
        char dot = '\u00B0';
        if(requests.isEmpty()){
            System.out.println("This user has no active friend requests!");
        }
        else{
            System.out.println("All friend requests of the user: ");
            requests.forEach(r -> System.out.println(dot + " " + r.getSender().getFirstName() + " " + r.getSender().getLastName() + " | " + r.getStatus()));
        }
    }

    private void respondToRequest(BufferedReader reader) throws IOException {
        String[] names = readSenderAndReceiver(reader);
        System.out.print("New status (APPROVE or REJECT): ");
        String status = reader.readLine();
        srv.respondFriendRequest(names[0],names[1],names[2],names[3],status);
        System.out.println("Friend request updated successfully!");
    }

    private void getConversation(BufferedReader reader) throws IOException {
        System.out.print("Give first user's first name: ");
        String user1FirstName = reader.readLine();
        System.out.print("Give first user's last name: ");
        String user1LastName = reader.readLine();
        System.out.print("Give second user's first name: ");
        String user2FirstName = reader.readLine();
        System.out.print("Give second user's last name: ");
        String user2LastName = reader.readLine();
        List<Message> messages = srv.getFullConversation(new Tuple<>(user1FirstName,user1LastName), new Tuple<>(user2FirstName,user2LastName));
        if(messages.isEmpty()){
            System.out.println("These users don't have a conversation!");
        }
        else{
            System.out.println();
            messages.forEach(m -> System.out.println(
                    m.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + " " +
                    m.getDate().format(DateTimeFormatter.ISO_LOCAL_TIME) + " | " +
                    m.getFromUser().getFirstName() + " " + m.getFromUser().getLastName() + ": " + m.getMessage()));
        }
    }

    private void sendMessage(BufferedReader reader) throws IOException{
        System.out.print("Give your first name: ");
        String user1FirstName = reader.readLine();
        System.out.print("Give your last name: ");
        String user1LastName = reader.readLine();
        boolean STOP = true;
        List<Tuple<String>> toUsers = new ArrayList<>();
        while(STOP){
            System.out.print("Give the first name of the user you want to send the message: ");
            String user2FirstName = reader.readLine();
            System.out.print("Give the last name of the user you want to send the message: ");
            String user2LastName = reader.readLine();
            toUsers.add(new Tuple<>(user2FirstName,user2LastName));
            System.out.println("Do you want to continue adding people?");
            System.out.print("1.Yes.\n2.No.\nIntroduce the command: ");
            try{
                int resp = Integer.parseInt(reader.readLine());
                if(resp == 2){
                    STOP = false;
                }
                else{
                    if(resp!=1){
                        throw new IllegalArgumentException("Invalid command!");
                    }
                }
            }
            catch (NumberFormatException e){
                throw new IllegalArgumentException("Command should be 1 or 2!");
            }
        }
        System.out.print("Type your message: ");
        String message = reader.readLine();
        //srv.sendMessage(user1FirstName,user1LastName,toUsers,message);
        System.out.println("Message sent successfully!");
    }

    private void replyMessage(BufferedReader reader) throws IOException {
        System.out.print("Give your first name: ");
        String userFirstName = reader.readLine();
        System.out.print("Give your last name: ");
        String userLastName = reader.readLine();
        System.out.print("Give message id: ");
        Long messageID = Long.parseLong(reader.readLine());
        System.out.print("Type your message: ");
        String messageText = reader.readLine();
        srv.replyMessage(userFirstName,userLastName,messageID,messageText);
        System.out.println("Reply sent successfully!");
    }

    private void replyAll(BufferedReader reader ) throws  IOException{
        System.out.print("Give your first name: ");
        String userFirstName = reader.readLine();
        System.out.print("Give your last name: ");
        String userLastName = reader.readLine();
        System.out.print("Give message id: ");
        Long messageID = Long.parseLong(reader.readLine());
        System.out.print("Type your message: ");
        String messageText = reader.readLine();
        srv.replyAll(userFirstName,userLastName,messageID,messageText);
        System.out.println("Reply sent successfully!");
    }
}
