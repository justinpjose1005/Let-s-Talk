package com.justinpjose.chatroom;

public class User {
    String name, email, password, status;
    long last_read_line;

    public User() {
    }

    public User(String name, String email, String password, String status, long last_read_line) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
        this.last_read_line = last_read_line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLast_read_line() {
        return last_read_line;
    }

    public void setLast_read_line(long last_read_line) {
        this.last_read_line = last_read_line;
    }
}
