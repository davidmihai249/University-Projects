package com.example.socialnetworkgui.utils.events;

import com.example.socialnetworkgui.domain.User;

public class UserFriendChangeEvent implements Event{
    private ChangeEventType type;
    private User data, oldData;

    public UserFriendChangeEvent(ChangeEventType type, User data) {
        this.type = type;
        this.data = data;
    }

    public UserFriendChangeEvent(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
