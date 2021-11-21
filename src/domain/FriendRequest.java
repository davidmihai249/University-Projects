package domain;

public class FriendRequest extends Entity<Tuple<Long>> {
    private RequestStatus status;

    public FriendRequest(Long senderID, Long receiverID, RequestStatus status) {
        setId(new Tuple<>(senderID,receiverID));
        this.status = status;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
