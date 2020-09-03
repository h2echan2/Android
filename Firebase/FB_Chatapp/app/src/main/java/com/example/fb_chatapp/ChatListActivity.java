package com.example.fb_chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    ListView listView;
    List<Chatroom> datas = new ArrayList<>();
    ListAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference DBref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Intent intent = getIntent();
        final String userID = intent.getExtras().getString("UserID");

        database = FirebaseDatabase.getInstance();
        DBref = database.getReference("chat");
        DBref.addValueEventListener(roomListener);

        listView = (ListView)findViewById(R.id.list_view);
        adapter = new ListAdapter(datas, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chatroom room = datas.get(position);
                Intent intent = new Intent(ChatListActivity.this, ChatroomActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("roomID", room.getRoomID());
                intent.putExtra("roomName",room.getRoomName());
                startActivity(intent);;
            }

        });
    }

    ValueEventListener roomListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            datas.clear();
            Log.d("snapdhot",snapshot.toString());
            for(DataSnapshot datasnapshot : snapshot.getChildren()){
                Chatroom room = new Chatroom();
                room.setRoomID(datasnapshot.child("roomID").getValue().toString());
                room.setRoomName(datasnapshot.child("roomName").getValue().toString());
                datas.add(room);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

}
//커스텀 리스트뷰는 데이터도 새로 정의한 Chatroom 클래스로 확장되어 ListView 아이템도 여러 위젯으로 구성 가능.
//따라서 Adapter 기능도 확장해야함.
//Adapter를 새롭게 구현할 때 안드로이드 SDK에서 제공하는 Adpter중 어떤 Adapter 중 어떤 Adapter 클래스를 부모로 사용할지 결정
//해야함. 보통 Base나 ArrayAdapter 사용.

class ListAdapter extends BaseAdapter{
    Context context;
    List<Chatroom> datas;
    LayoutInflater inflater;

    public ListAdapter(List<Chatroom> datas, Context context){
        this.context = context;
        this.datas = datas;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return datas.size();
    }
    // potistion에 위치한 데이터를 화면에 출력하는데 사용될 Viewfmf flxjs.
    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 V
    @Override
    public long getItemId(int position) {
        return position;
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //listview 레이아웃을 inflate하여 convertview 참조 획득.
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_roomlist, null);
        }

        //
        Chatroom room = datas.get(position);
        TextView roomTitle = (TextView) convertView.findViewById(R.id.roomTitle_chatList);
        roomTitle.setText(room.getRoomName());
        Log.d("title", roomTitle.getText().toString());
        Log.d("ID", room.getRoomID());
        return convertView;
    }
}