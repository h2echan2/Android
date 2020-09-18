package com.example.fb_chatapp;

public class EncMessage {

    private String LoginID;
    private String encMessage;
    private String recieveUserID;

    public EncMessage(){}

    public EncMessage(String loginID, String encMessage, String recieveUserID) {
        this.LoginID = loginID;
        this.encMessage = encMessage;
        this.recieveUserID = recieveUserID;
    }

    public String getLoginID() {
        return LoginID;
    }

    public void setLoginID(String loginID) {
        LoginID = loginID;
    }

    public String getEncMessage() {
        return encMessage;
    }

    public void setEncMessage(String encMessage) {
        this.encMessage = encMessage;
    }

    public String getRecieveUserID() {
        return recieveUserID;
    }

    public void setRecieveUserID(String recieveUserID) {
        this.recieveUserID = recieveUserID;
    }
}
