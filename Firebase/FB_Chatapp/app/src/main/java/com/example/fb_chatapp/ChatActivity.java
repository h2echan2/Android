package com.example.fb_chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {


    User loginUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //todo ViewGroup Init
        Button Roomlist = (Button) findViewById(R.id.ChatlistButton_Chat);
        Button RoomIn = (Button) findViewById(R.id.ChatinButton_Chat);
        TextView ChatID = (TextView) findViewById(R.id.UserID_Chat);
        Button Userlist = (Button) findViewById(R.id.UserList_Chat);
        final TextView ChatName = (TextView) findViewById(R.id.UserName_chat);

        //todo FirebaseDB에서 유저네임 아이디 가져와서 텍스트보여주기
        Intent intent1 = getIntent();
        final String UserID = intent1.getExtras().getString("UserID");
        ChatID.setText("ID: "+UserID);

        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("users");
        DBref.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    String name = snapshot.child("username").getValue().toString();
                    ChatName.setText("Name: "+ name);

                    loginUser.setID(snapshot.child("ID").getValue().toString());
                    loginUser.setPassword(snapshot.child("Password").getValue().toString());
                    loginUser.setPrivateKey(snapshot.child("privateKey").getValue().toString());
                    loginUser.setPublicKey(snapshot.child("publicKey").getValue().toString());
                    loginUser.setUsername(snapshot.child("username").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo 채팅방 목록버튼: 클릭시 채팅방 목록이 나와서, 채팅방 클릭시 채팅방 입장.
        Roomlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                intent.putExtra("UserID",UserID);
                startActivity(intent);
            }
        });

        //todo 채팅방 개설버튼: 클릭시 채팅방 이름을 입력받아, 채팅방을 개설
        RoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        //todo. 유저목록 버튼 1대1 대화메시지 구현을 위해 생성.
        Userlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), UserListActivity.class);

                intent.putExtra("LoginUserID", loginUser.getID());
                intent.putExtra("LoginUserPrivateKey", loginUser.getPrivateKey());
                intent.putExtra("LoginUserPublicKey", loginUser.getPublicKey());
                intent.putExtra("LoginUserPassword", loginUser.getPassword());
                intent.putExtra("LoginUserName", loginUser.getUsername());

                startActivity(intent);
            }
        });
    }
    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.newchatroom,null);
        builder.setView(view);

        Button makeChatroom = (Button) view.findViewById(R.id.makeRoom_chat);
        final EditText roomName = (EditText) view.findViewById(R.id.chatName_chat);


        final AlertDialog dialog = builder.create();
        makeChatroom.setOnClickListener(new View.OnClickListener() {

            //로그인 체크 기능 구현.
            @Override
            public void onClick(View v) {
                //todo 1. 채팅방 입력받아서 채팅방 객체 생성및 초기.
                String roomname = roomName.getText().toString();
                String RoomID = Integer.toString(roomname.hashCode());
                Log.d("test", RoomID);

                Chatroom chatroom = new Chatroom();
                chatroom.setRoomID(RoomID);
                chatroom.setRoomName(roomname);

                //todo 2. roomName을 기반으로해서 ID로 구분자두고 일단 개설.
                FBDB Db = new FBDB();
                DatabaseReference DBRef = Db.getDBInstance().getReference();
                if(DBRef != null){
                    Log.d("dbref","notnull");
                }

                DBRef.child("chat").child(roomname).setValue(chatroom);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
