package com.example.fb_chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatroomActivity extends AppCompatActivity {

    ListView chat_view;
    EditText chat_edit;
    Button chat_send;

    FirebaseDatabase FbDB = FirebaseDatabase.getInstance();
    DatabaseReference DBref = FbDB.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        chat_view = (ListView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);

        Intent intent = getIntent();
        final String userID = intent.getExtras().getString("userID");
        String roomID = intent.getExtras().getString("roomID");
        final String roomName = intent.getExtras().getString("roomName");

        Log.d("test0","test0");
        openChat(roomName);
        Log.d("test1","test1");
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chat_edit.getText().toString().equals(""))
                    return;

                ChatData chatData = new ChatData(userID, chat_edit.getText().toString());
                DBref.child("Chat_Message").child(roomName).push().setValue(chatData);
                chat_edit.setText("");
            }
        });
    }

private void addMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter){
    ChatData chatdata = dataSnapshot.getValue(ChatData.class);
    adapter.add(chatdata.getUserID() + " : " + chatdata.getMessage());
}

private void openChat(String chatName){
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
    chat_view.setAdapter(adapter);

    DBref.child("Chat_Message").child(chatName).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            addMessage(snapshot, adapter);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
}