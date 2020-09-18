package com.example.fb_chatapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatroomActivity extends AppCompatActivity {

    RecyclerView chat_view;
    EditText chat_edit;
    Button chat_send;
    ImageView Profile1;
    Drawable profileImg1;
    Drawable profileImg2;
    String userID;

    private ArrayList<ChatData> datalist = new ArrayList<ChatData>();

    FirebaseDatabase FbDB = FirebaseDatabase.getInstance();
    DatabaseReference DBref = FbDB.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        chat_view = (RecyclerView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);
        Profile1 = (ImageView) findViewById(R.id.Userprofile_img);


        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        String roomID = intent.getExtras().getString("roomID");
        final String roomName = intent.getExtras().getString("roomName");
        profileImg1 = getResources().getDrawable(R.drawable.cat1);
        profileImg2 = getResources().getDrawable(R.drawable.cat2);
        //기본적으로 DB의 메세지는 false;
        final boolean isMymessage = false;

        openChat(roomName);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        chat_view.setLayoutManager(manager);

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

private void openChat(String chatName){
    //final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
    final Myadapter adapter = new Myadapter(datalist);
    chat_view.setAdapter(adapter);

    DBref.child("Chat_Message").child(chatName).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            ChatData chatdata = snapshot.getValue(ChatData.class);

            if(chatdata.getUserID().equals(userID)){
                chatdata.setIcon(profileImg1);
                chatdata.setMydata(true);
            }
            else{
                chatdata.setIcon(profileImg2);
            }
            datalist.add(chatdata);
            adapter.addItem(datalist);
            adapter.notifyDataSetChanged();
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

public class Myadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<ChatData> chatDataArrayList = null;

        Myadapter(ArrayList<ChatData> datalist){
            chatDataArrayList = datalist;
        }

        public class LeftViewHolder extends RecyclerView.ViewHolder{
            TextView content;
            TextView name;
            ImageView image;

            public LeftViewHolder(@NonNull View itemView) {
                super(itemView);

                content = itemView.findViewById(R.id.comment_leftform);
                name =  itemView.findViewById(R.id.userID_leftform);
                image = itemView.findViewById(R.id.Userprofile_img);
            }
        }

        public class RightViewHolder extends RecyclerView.ViewHolder{
            TextView content;
            TextView name;
            ImageView image;

            public RightViewHolder(@NonNull View itemView) {
                super(itemView);
                content = itemView.findViewById(R.id.comment_rightform);
                name = itemView.findViewById(R.id.userID_rightform);
                image = itemView.findViewById(R.id.Userprofile_img_rightform);
            }
        }

    //뷰홀더 객체 생성.
    @NonNull
   @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewType.Left_contents){
            Log.d("viewtype", "left");
            view = inflater.inflate(R.layout.chat_leftform, parent, false);
            return new LeftViewHolder(view);
        }
        else{
            view = inflater.inflate(R.layout.chat_rightform, parent, false);
            Log.d("viewtype", "right");
            return new RightViewHolder(view);
        }
    }

    //데이터를 뷰홀더에 바인딩.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LeftViewHolder){
            ((LeftViewHolder) holder).content.setText(chatDataArrayList.get(position).getMessage());
            ((LeftViewHolder) holder).name.setText(chatDataArrayList.get(position).getUserID());
            ((LeftViewHolder) holder).image.setImageDrawable(chatDataArrayList.get(position).getIcon());
        }
        else{
            ((RightViewHolder) holder).content.setText(chatDataArrayList.get(position).getMessage());
            ((RightViewHolder) holder).name.setText(chatDataArrayList.get(position).getUserID());
            ((RightViewHolder) holder).image.setImageDrawable(chatDataArrayList.get(position).getIcon());
        }
    }

    @Override
    public  int getItemViewType(int position){
            if(chatDataArrayList.get(position).getMydata() == false){
                return ViewType.Right_contents;
            }
            else
                return ViewType.Left_contents;
    }

    @Override
    public int getItemCount() {
        return chatDataArrayList.size();
    }

    public void addItem(ArrayList<ChatData> item){
            chatDataArrayList = item;
    }
}

public class ViewType{
        public static final int Left_contents = 0;
        public static  final int Right_contents = 1;
}

/*
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
*/
}