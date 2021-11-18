package ui;

import domain.FriendDTO;
import org.jetbrains.annotations.NotNull;
import domain.User;
import domain.validators.FriendshipException;
import domain.validators.ValidationException;
import service.UserFriendshipService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

public class ConsoleInterface {
    private final UserFriendshipService srv;

    public ConsoleInterface(UserFriendshipService srv) {
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
                    default -> System.out.println("Invalid command!");
                }
            }
            catch (ValidationException | IllegalArgumentException | FriendshipException e){
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
        System.out.print("Give friendships month: ");
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
            friendDTOS.forEach(f-> System.out.println(f.getFriend().getFirstName() + "|" + f.getFriend().getLastName() + "|" + f.getDate()));
        }
    }
}
