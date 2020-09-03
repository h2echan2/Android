package com.example.fb_chatapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatroomActivity extends AppCompatActivity {

    ListView chat_view;
    EditText chat_edit;
    Button chat_send;
    ImageView Profile1;
    Drawable profileImg1;
    Drawable profileImg2;


    FirebaseDatabase FbDB = FirebaseDatabase.getInstance();
    DatabaseReference DBref = FbDB.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        chat_view = (ListView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);
        Profile1 = (ImageView) findViewById(R.id.Userprofile_img);


        Intent intent = getIntent();
        final String userID = intent.getExtras().getString("userID");
        String roomID = intent.getExtras().getString("roomID");
        final String roomName = intent.getExtras().getString("roomName");
        profileImg1 = getResources().getDrawable(R.drawable.cat1);
        profileImg2 = getResources().getDrawable(R.drawable.cat2);
        //기본적으로 DB의 메세지는 false;
        final boolean isMymessage = false;


        Log.d("test0","test0");
        openChat(roomName);
        Log.d("test1","test1");
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chat_edit.getText().toString().equals(""))
                    return;

                ChatData chatData = new ChatData(userID, chat_edit.getText().toString(), isMymessage);
                DBref.child("Chat_Message").child(roomName).push().setValue(chatData);
                chat_edit.setText("");
            }
        });
    }

private void addMessage(DataSnapshot dataSnapshot, CustomAdapter adapter){
    ChatData chatdata = dataSnapshot.getValue(ChatData.class);
    chatdata.setIcon(profileImg1);
    adapter.add(chatdata);
    adapter.notifyDataSetChanged();
}

private void openChat(String chatName){
    //final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

    final CustomAdapter adapter = new CustomAdapter();

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

public class CustomAdapter extends BaseAdapter{

        //채팅을 세트로 담기위한 어레이
    private ArrayList<ChatData> Chatdata = new ArrayList<>();

    //리스트뷰에 보여질 아이템 수
    @Override
    public int getCount() {
        return Chatdata.size();
    }

    //하나의 Item
    @Override
    public Object getItem(int position) {
        return Chatdata.get(position);
    }

    //Item을 구별하기 위한것으로 포지션으로 구별.
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_leftform, parent, false);
        }

        //chat_leftform에 정의된 위젯 참조
        ImageView adapter_img = (ImageView) convertView.findViewById(R.id.Userprofile_img);
        TextView adapter_ID = (TextView) convertView.findViewById(R.id.userID_leftform);
        TextView adapter_message = (TextView) convertView.findViewById(R.id.comment_leftform);

        ChatData mydata = (ChatData) getItem(position);

        adapter_ID.setText(mydata.getUserID());
        adapter_img.setImageDrawable(mydata.getIcon());
        adapter_message.setText(mydata.getMessage());

        return convertView;
    }

    public void add(ChatData data){
        ChatData chatdata = new ChatData();

        chatdata.setIcon(data.getIcon());
        chatdata.setMessage(data.getMessage());
        chatdata.setUserID(data.getUserID());
        chatdata.setMydata(data.getMydata());

        Chatdata.add(chatdata);
    }
}
}