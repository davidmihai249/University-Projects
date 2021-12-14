package com.example.socialnetworkgui.domain;

import java.time.LocalDate;

public class FriendRequestDTO {
    private User sender;
    private RequestStatus status;
    private LocalDate date;

    public FriendRequestDTO(User sender, RequestStatus status) {
        this.sender = sender;
        this.status = status;
    }

    public FriendRequestDTO(User sender, RequestStatus status,LocalDate date) {
        this.sender = sender;
        this.status = status;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
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
