package com.example.fb_chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.ActionMode;
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

public class UserListActivity extends AppCompatActivity {

    ListView listView;
    List<User> datas = new ArrayList<>();
    ListAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference DBref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        String LoginUserID = intent.getExtras().getString("LoginUserID");
        String LoginUserPassword = intent.getExtras().getString("LoginUserPassword");
        String LoginUserPrivateKey = intent.getExtras().getString("LoginUserPrivateKey");
        String LoginUserPublicKey = intent.getExtras().getString("LoginUserPublicKey");
        String LoginUserName = intent.getExtras().getString("LoginUserName");

        final User loginUser = new User(LoginUserName, LoginUserID , LoginUserPassword, LoginUserPublicKey, LoginUserPrivateKey);


        database = FirebaseDatabase.getInstance();
        DBref = database.getReference("users");
        DBref.addValueEventListener(UserListener);

        listView = (ListView)findViewById(R.id.user_list_view);
        adapter = new ListAdapter(datas, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = datas.get(position);
                Intent intent = new Intent(UserListActivity.this, u2uChatActivity.class);

                intent.putExtra("LoginUserID", loginUser.getID());
                intent.putExtra("LoginUserPrivateKey", loginUser.getPrivateKey());
                intent.putExtra("LoginUserPublicKey", loginUser.getPublicKey());
                intent.putExtra("LoginUserPassword", loginUser.getPassword());
                intent.putExtra("LoginUserName", loginUser.getUsername());

                intent.putExtra("SelectedUserID", user.getID());
                intent.putExtra("SelectedUserPrivateKey", user.getPrivateKey());
                intent.putExtra("SelectedUserPublicKey", user.getPublicKey());
                intent.putExtra("SelectedUserPassword", user.getPassword());
                intent.putExtra("SelectedUserName", user.getUsername());

                startActivity(intent);
            }
        });

    }

    ValueEventListener UserListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            datas.clear();
            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                User user = new User();
                user.setID(dataSnapshot.child("ID").getValue().toString());
                user.setPassword(dataSnapshot.child("Password").getValue().toString());
                user.setPrivateKey(dataSnapshot.child("privateKey").getValue().toString());
                user.setPublicKey(dataSnapshot.child("publicKey").getValue().toString());
                user.setUsername(dataSnapshot.child("username").getValue().toString());
                datas.add(user);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    class ListAdapter extends BaseAdapter{
        Context context;
        List<User> datas;
        LayoutInflater inflater;

        public ListAdapter(List<User> datas, Context context){
            this.context = context;
            this.datas = datas;
            this.inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(R.layout.userlist, null);
            }

            User user = datas.get(position);
            TextView userList = (TextView) convertView.findViewById(R.id.userID_userList);
            userList.setText(user.getID());

            return convertView;
        }
    }

}
