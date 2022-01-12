package com.example.socialnetworkgui.domain;

import com.example.socialnetworkgui.service.UserFriendshipDbService;

import java.util.List;

public class Page {
    private String firstName;
    private String lastName;
    private List<FriendDTO> friendsList;
    private List<Message> receivedMessages;
    private List<FriendRequestDTO> friendRequests;
    private UserFriendshipDbService service;

    public Page(String firstName, String lastName, List<FriendDTO> friendsList, List<Message> receivedMessages, List<FriendRequestDTO> friendRequests) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsList = friendsList;
        this.receivedMessages = receivedMessages;
        this.friendRequests = friendRequests;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public User getUser(){
        return service.getUser(this.firstName, this.lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<FriendDTO> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<FriendDTO> friendsList) {
        this.friendsList = friendsList;
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public List<FriendRequestDTO> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<FriendRequestDTO> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public UserFriendshipDbService getService() {
        return service;
    }

    public void setService(UserFriendshipDbService service){
        this.service = service;
    }

    public void addFriend(String firstName, String lastName){
        service.addFriend(this.firstName, this.lastName, firstName, lastName);
    }

    public void removeFriend(String firstName, String lastName){
        service.removeFriend(this.firstName, this.lastName, firstName, lastName);
    }

    public List<FriendRequestDTO> findSentRequests(){
        return service.getUsersSentRequests(this.firstName, this.lastName);
    }

    public List<FriendRequestDTO> findReceivedRequests(){
        return service.getUsersRequests(this.firstName, this.lastName);
    }

    public void unsendFriendRequest(String firstName, String lastName){
        User friend = service.getUser(firstName,lastName);
        service.removeFriendRequest(this.firstName, this.lastName, friend.getFirstName(), friend.getLastName());
    }
}
