package com.example.fb_chatapp;

import android.app.Activity;
import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class LoginActivity extends Activity {
    Button Loginbutton;
    Button Signbutton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loginActivty와 연결
        setContentView(R.layout.activity_login);

        //버튼 초기화 및 리스너
        this.InitializeButton();
        this.SetListener();

        //
    }

    public void InitializeButton(){
        Loginbutton  = (Button) findViewById(R.id.login_button);
        Signbutton  = (Button) findViewById(R.id.sign_button);
    }

    public void SetListener(){
        Loginbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        Signbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
