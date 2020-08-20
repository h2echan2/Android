package com.example.fb_chatapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBDB {

    //DB 객체 생성 불러오기.
    public FirebaseDatabase getDBInstance(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(database != null){
            System.out.println("success");
        }
        return database;
    }

}
