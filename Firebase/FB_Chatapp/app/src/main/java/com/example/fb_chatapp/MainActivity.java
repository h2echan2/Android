package com.example.fb_chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Handler delayHandler = new Handler();

        for(i=0;i<101;i++){
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.incrementProgressBy(i);
                }
            },3000);
        }

        this.getDBInstance();

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
    }

    //파이어베이스 디비 가져오기.
    public void getDBInstance(){
        FBDB fbDB = new FBDB();
        FirebaseDatabase fbdb = fbDB.getDBInstance();
    }
}
