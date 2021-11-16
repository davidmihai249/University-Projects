import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import repository.Repository;
import repository.db.FriendshipDbRepo;
import repository.db.UserDbRepo;
import service.UserFriendshipDbService;
import ui.ConsoleInterface;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        //Repository<Long, User> userRepo = new UserFile("data/user.in", new UserValidator());
        //Repository<Tuple<Long>, Friendship> friendshipRepo = new FriendshipFile("data/friendship.in", new FriendshipValidator());
        //UserFriendshipService srv = new UserFriendshipService(userRepo, friendshipRepo);

        Repository<Long,User> userRepo = new UserDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "Pikamar77",
                new UserValidator());
        Repository<Tuple<Long>, Friendship> friendshipRepo = new FriendshipDbRepo(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "Pikamar77",
                new FriendshipValidator());
        UserFriendshipDbService srv = new UserFriendshipDbService(userRepo, friendshipRepo);

        ConsoleInterface ui = new ConsoleInterface(srv);
        ui.run();
    }
}
