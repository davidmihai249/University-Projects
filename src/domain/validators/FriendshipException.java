package domain.validators;

public class FriendshipException extends RuntimeException{
    public FriendshipException() {
    }

    public FriendshipException(String message) {
        super(message);
    }
}
