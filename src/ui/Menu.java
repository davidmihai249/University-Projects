package ui;

public class Menu {
    private static final String[] menu = {
            " 1. Add user",
            " 2. Remove user",
            " 3. Show all users",
            " 4. Add friend",
            " 5. Remove friend",
            " 6. Show all friends of a user",
            " 7. Get communities number",
            " 8. Get the biggest community",
            " 9. Show all friendships created in a certain month",
            "10. Show all friends of a certain user.",
            "11. Send a new friend request",
            "12. See all friend requests of a user",
            "13. Respond to a friend request",
            "X. Close application"};

    public static void printMenu(){
        System.out.println("\n------- MENU -------");
        for(String option : menu){
            System.out.println(option);
        }
    }
}
