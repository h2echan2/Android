package com.example.fb_chatapp;

public class User {
    public String username;
    public String ID;
    public String Password;
    public String publicKey;
    public String privateKey;

    public User(){

    }

    public User(String username, String email, String Pwd, String publicKey, String privateKey){
        this.username = username;
        this.ID = email;
        this.Password = Pwd;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
