package domain;

import java.time.LocalDate;

public class FriendDTO {
    private User friend;
    private LocalDate date;

    public FriendDTO(User first_user, LocalDate date) {
        this.friend = first_user;
        this.date = date;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
