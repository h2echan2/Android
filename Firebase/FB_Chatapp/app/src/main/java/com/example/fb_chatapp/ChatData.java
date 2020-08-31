package com.example.fb_chatapp;

public class ChatData {

    private String userID;
    private String message;

    public ChatData() {}

    public ChatData(String userID, String message) {
        this.userID = userID;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setMessage(String message){

        this.message = message;
    }
}
