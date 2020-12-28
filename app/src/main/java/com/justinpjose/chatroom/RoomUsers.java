package com.justinpjose.chatroom;

public class RoomUsers {
    String name, status;

    public RoomUsers(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public RoomUsers() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
