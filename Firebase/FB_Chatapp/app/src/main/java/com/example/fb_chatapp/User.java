package com.example.fb_chatapp;

public class User {
    public String username;
    public String ID;
    public String Password;

    public User(){

    }

    public User(String username, String email, String Pwd){
        this.username = username;
        this.ID = email;
        this.Password = Pwd;
    }

}
