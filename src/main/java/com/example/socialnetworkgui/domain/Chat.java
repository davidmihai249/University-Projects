package com.example.socialnetworkgui.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chat extends Entity<Long> {
    private String chatName;
    private List<Long> users;

    public Chat(String chatName){
        this.chatName = chatName;
        this.users = new ArrayList<>();
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(chatName, chat.chatName) && Objects.equals(users, chat.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatName, users);
    }
}
