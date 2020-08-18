package com.example.fb_chatapp;

public class ChatData {
    private String userName;
    private String message;

    public ChatData(){}
    public ChatData(String userName, String message){
        this.userName = userName;
        this.message = message;
    }

    public void setuserName(String userName){
        this.userName = userName;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
