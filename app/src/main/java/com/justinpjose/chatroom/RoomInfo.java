package com.justinpjose.chatroom;

public class RoomInfo {
    String name,code,id;

    public RoomInfo(String name, String code,String id) {
        this.name = name;
        this.code = code;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RoomInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
