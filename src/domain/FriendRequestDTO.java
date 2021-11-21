package domain;

public class FriendRequestDTO {
    private User sender;
    private RequestStatus status;

    public FriendRequestDTO(User sender, RequestStatus status) {
        this.sender = sender;
        this.status = status;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
