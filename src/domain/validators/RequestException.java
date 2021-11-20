package domain.validators;

public class RequestException extends RuntimeException{
    public RequestException() {
    }

    public RequestException(String message) {
        super(message);
    }
}
