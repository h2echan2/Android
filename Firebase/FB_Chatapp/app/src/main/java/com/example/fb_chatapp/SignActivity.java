package com.example.fb_chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignActivity extends AppCompatActivity {

    EditText Name_Sign;
    EditText ID_Sign;
    EditText Password_Sign;
    Button signButton;
    DatabaseReference DBRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        //뷰그룹 초기화
        initView();

        //회원가입 데이터 저장을 위해 DBref를 가져옴.
        DBRef = FirebaseDatabase.getInstance().getReference();

        //회원가입 버튼 클릭 시 처리.
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Name_Sign.getText().toString();
                String ID = ID_Sign.getText().toString();
                String Password = Password_Sign.getText().toString();

                if(name.length() == 0 || ID.length()==0 || Password.length() ==0){
                    Toast.makeText(SignActivity.this, "Invalid parameters", Toast.LENGTH_SHORT).show();
                }
                else{
                    User user = new User(name,ID,Password);
                    DBRef.child("users").child(ID).setValue(user);
                    Toast.makeText(SignActivity.this, "회원가입이 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    public void initView(){
        Name_Sign = findViewById(R.id.Name_Sign);
        ID_Sign = findViewById(R.id.ID_SIgn);
        Password_Sign = findViewById(R.id.Password_Sign);
        signButton = findViewById(R.id.signButton_Sign);
    }
}
