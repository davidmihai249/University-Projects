package com.example.socialnetworkgui.domain;

import java.time.LocalDate;

public class FriendRequest extends Entity<Tuple<Long>> {
    private RequestStatus status;
    private LocalDate date;

    public FriendRequest(Long senderID, Long receiverID, RequestStatus status) {
        setId(new Tuple<>(senderID,receiverID));
        this.status = status;
    }

    public FriendRequest(Long senderID,Long receiverID,RequestStatus status,LocalDate date){
        setId(new Tuple<>(senderID,receiverID));
        this.status = status;
        this.date = date;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
