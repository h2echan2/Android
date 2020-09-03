package com.example.fb_chatapp;

import android.graphics.drawable.Drawable;

public class ChatData {

    private boolean Mydata ;
    private Drawable Icon;
    private String userID;
    private String message;


    public ChatData() {}

    public ChatData( String userID, String message, boolean Mydata) {
        this.userID = userID;
        this.message = message;
        this.Mydata = Mydata;
    }

    public ChatData(Drawable Icon, String userID, String message, boolean Mydata) {
        this.Icon = Icon;
        this.userID = userID;
        this.message = message;
        this.Mydata = Mydata;
    }

    public Drawable getIcon() { return Icon; }
    public String getMessage() {
        return message;
    }
    public String getUserID() {
        return userID;
    }
    public boolean getMydata() { return Mydata; }


    public void setMydata(boolean mydata) { Mydata = mydata; }
    public void setIcon(Drawable icon) { Icon = icon; }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setMessage(String message){ this.message = message; }
}
