package com.example.fb_chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends Activity {
    Button Loginbutton;
    Button Signbutton;

    String UserID;
    String UserPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loginActivty와 연결
        setContentView(R.layout.activity_login);

        //버튼 초기화 및 리스너 설정
        this.InitializeButton();

        //todo 1.
        Loginbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                show();
            }


        });

        //회원가입 기능 구현 완료.
        Signbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignActivity.class);
                startActivity(intent);
            }
        });



        //todo 로그인 기능


    }

    public void InitializeButton(){
        Loginbutton  = (Button) findViewById(R.id.login_button);
        Signbutton  = (Button) findViewById(R.id.sign_button);
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogfragment,null);
        builder.setView(view);

        Button Login_dialog = (Button) view.findViewById(R.id.loginbutton_dialog);
        final EditText ID_dialog = (EditText) view.findViewById(R.id.IDtext_dialog);
        final EditText Pwd_dialog = (EditText) view.findViewById(R.id.pwdtext_dialog);

        final AlertDialog dialog = builder.create();
        Login_dialog.setOnClickListener(new View.OnClickListener() {

            //로그인 체크 기능 구현.
            @Override
            public void onClick(View v) {
                //todo 1. 로그인 창에서 아이디랑 패스워드 정보 가져오기.
                final String ID = ID_dialog.getText().toString();
                final String Pwd = Pwd_dialog.getText().toString();
                Log.d("Tag",ID);

                //todo 2. FBDB 스냅샷 가져와서 정보 기반으로해서 아이디랑 패스워드 매칭되는게 있는 지 확인.
                FBDB Db = new FBDB();
                DatabaseReference DBRef = Db.getDBInstance().getReference("users");
                if(DBRef != null){
                    Log.d("dbref","notnull");
                }

                DBRef.child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            String fbPw = snapshot.child("Password").getValue().toString();
                            Log.d("pwdCheck",fbPw);
                            if(fbPw.equals(Pwd)){
                                Log.d("test",ID);

                                dialog.dismiss();
                                //id넘겨줘야함

                                Intent intent1 = new Intent(getApplicationContext(), ChatActivity.class);
                                intent1.putExtra("UserID",ID);
                                startActivity(intent1);

                                Toast.makeText(LoginActivity.this,"비밀번호 정보가 맞습니다.",Toast.LENGTH_SHORT ).show();
                            }
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this,"비밀번호 정보가 틀립니다.",Toast.LENGTH_SHORT ).show();
                        }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

            }
        });

        dialog.show();
    }

}
