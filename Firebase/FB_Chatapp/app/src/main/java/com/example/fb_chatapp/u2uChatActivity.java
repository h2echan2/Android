package com.example.fb_chatapp;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.prefs.PreferenceChangeEvent;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class u2uChatActivity extends AppCompatActivity {


    RecyclerView chat_view;
    EditText chat_edit;
    Button chat_send;
    //ImageView Profile1;
    Drawable profileImg1;
    Drawable profileImg2;

    String suPublickey;
    String suID;
    String loginID;
    String loginPrivateKey;
    String loginPublicKey;
    String loginPassword;
    String loginUsername;


    private ArrayList<String> previousChatdata = new ArrayList<>();
    private ArrayList<String> isMydata = new ArrayList<>();
    private ArrayList<ChatData> datalist = new ArrayList<>();

    FirebaseDatabase FbDB = FirebaseDatabase.getInstance();
    DatabaseReference DBref = FbDB.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u2u_chat);

        //위에 선언한 view들 초기화.
        initView();

        profileImg1 = getResources().getDrawable(R.drawable.cat1);
        profileImg2 = getResources().getDrawable(R.drawable.cat2);

        //Intent로 받아올 내용들 받기
        Intent intent = getIntent();
        getIntents(intent);

        //위의 과정에서 datalist에 업로드된 기존 저장 Chatdata
        final Myadapter adapter = new Myadapter(datalist);
        chat_view.setAdapter(adapter);

        //기존의 대화메시지들 가져오기
        AdaptPreviousChat(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chat_view.setLayoutManager(manager);

        //채팅 전송 버튼 클릭시
        chat_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if(chat_edit.getText().toString().equals(""))
                    return;

                String plainMessage = chat_edit.getText().toString();

                //Sharedpreference에 신규 메시지 추가.
                previousChatdata.add(plainMessage);
                isMydata.add("true");
                setStringArrayPref(loginID+suID, previousChatdata);
                setIsmydata(loginID+suID, isMydata);
                //암호화 및 FBDB 업로드.
                String encryptedMessage = encrypt(plainMessage, suPublickey);
                EncMessage encMessage = new EncMessage(loginID, encryptedMessage, suID);
                DBref.child("Enc_Message").child(loginID).child(suID).push().setValue(encMessage);
                chat_edit.setText("");

                ChatData chatdata = new ChatData(profileImg1, loginID, plainMessage, true);

                /*
                chatdata.setMessage(plainMessage);
                chatdata.setMydata(true);
                chatdata.setUserID(loginID);
                chatdata.setIcon(profileImg1);
                */


                datalist.add(chatdata);
                adapter.addItem(datalist);
                adapter.notifyDataSetChanged();
            }
        });


    }



    private void initView(){
        chat_view = (RecyclerView) findViewById(R.id.chat_view_u2u);
        chat_edit = (EditText) findViewById(R.id.chat_edit_u2u);
        chat_send = (Button) findViewById(R.id.chat_sent_u2u);
        //Profile1 = (ImageView) findViewById(R.id.Userprofile_img);
        profileImg1 = getResources().getDrawable(R.drawable.cat1);
        profileImg2 = getResources().getDrawable(R.drawable.cat2);
    }

    private void getIntents(Intent intent){
        suPublickey = intent.getExtras().getString("SelectedUserPublicKey");
        suID = intent.getExtras().getString("SelectedUserID");

        loginID = intent.getExtras().getString("LoginUserID");
        loginPrivateKey = intent.getExtras().getString("LoginUserPrivateKey");
        loginPublicKey = intent.getExtras().getString("LoginUserPublicKey");
        loginPassword = intent.getExtras().getString("LoginUserPassword");
        loginUsername = intent.getExtras().getString("LoginUsername");
    }

    private void AdaptPreviousChat(final Myadapter adapter){
        //Sharedpref에 저장된 기존의 데이터+ 데이터가 현재 로그인유저의 데이터 불리언값 Load해서 datalist에 저장.
        previousChatdata = getStringArrayPref(loginID+suID);
        isMydata = getIsmydata(loginID+suID);

        for(int i=0; i < previousChatdata.size();i++){
            ChatData chatdata = new ChatData();
            if(isMydata.get(i).equals("true")){
                chatdata.setMydata(true);
                chatdata.setUserID(loginID);
                chatdata.setIcon(profileImg1);
            }else if(isMydata.get(i).equals("false")){
                chatdata.setMydata(false);
                chatdata.setUserID(suID);
                chatdata.setIcon(profileImg2);
            }
            chatdata.setMessage(previousChatdata.get(i));
            datalist.add(chatdata);
            adapter.addItem(datalist);
            adapter.notifyDataSetChanged();
        }

        //상대방 채팅 메시지 오는거 리스너 등록
        DBref.child("Enc_Message").child(suID).child(loginID).addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ChatData chatData = new ChatData();
                EncMessage encMessage = snapshot.getValue(EncMessage.class);

                if(encMessage.getLoginID().equals(suID)){
                    chatData.setMessage(decrypt(encMessage.getEncMessage(), loginPrivateKey));
                    chatData.setIcon(profileImg2);
                    chatData.setUserID(suID);
                    chatData.setMydata(false);

                    //Sharedpreference에 신규 메시지 추가.
                    previousChatdata.add(decrypt(encMessage.getEncMessage(), loginPrivateKey));
                    isMydata.add("false");
                    setStringArrayPref(loginID+suID, previousChatdata);
                    setIsmydata(loginID+suID, isMydata);

                    DBref.child("Enc_Message").child(suID).child(loginID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("채팅 확인 및 삭제","삭제 성공");
                        }
                    });

                    //화면 출력을 위한 기능.
                    datalist.add(chatData);
                    adapter.addItem(datalist);
                    adapter.notifyDataSetChanged();
                }
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

    private void setIsmydata(String key, ArrayList<String> isMydata){
        SharedPreferences prefs = getSharedPreferences(loginID+suID+"isMydata", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for(int i=0; i<isMydata.size(); i++){
            a.put(isMydata.get(i));
        }
        if(!isMydata.isEmpty()){
            editor.putString(key, a.toString());
        } else{
          editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList getIsmydata(String key){
        SharedPreferences prefs = getSharedPreferences(loginID+suID+"isMydata", MODE_PRIVATE);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if(json != null){
            try{
                JSONArray a = new JSONArray(json);

                for(int i=0; i<a.length(); i++){
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private void setStringArrayPref(String key, ArrayList<String> values){

        SharedPreferences prefs = getSharedPreferences(loginID+suID, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i=0; i < values.size();i++){
            a.put(values.get(i));
        }
        if(!values.isEmpty()){
            editor.putString(key, a.toString());
        }else{
            editor.putString(key,null);
        }
        editor.apply();
    }

    private ArrayList getStringArrayPref(String key){
        SharedPreferences prefs = getSharedPreferences(loginID+suID, MODE_PRIVATE);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if(json != null){
            try{
                JSONArray a = new JSONArray(json);

                for(int i=0; i < a.length(); i++){
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    static String encrypt(String plaintext, String stringPublicKey){
        String encMessage = null;
        System.out.println("publickey: "+stringPublicKey);

        try{
            //평문으로 전달받은 공개키를 공개키 객체로 만드는 과정
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePublicKey = Base64.getDecoder().decode(stringPublicKey.getBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            //만들어진 공개키 객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            //평문 암호화 과정
            byte[] byteEncryptedData = cipher.doFinal(plaintext.getBytes());
            encMessage = Base64.getEncoder().encodeToString(byteEncryptedData);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encMessage;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static String decrypt(String encryptedData, String stringPrivateKey){
        String decMessage = null;

        try{
            //평문으로 전달받은 개인키를 개인키 객체로 만드는 과정
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            //만들어진 개인키 객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            //암호문을 복호화
            byte[] byteEncryptedData = Base64.getDecoder().decode(encryptedData.getBytes());
            byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
            decMessage = new String(byteDecryptedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decMessage;
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
            if(holder instanceof u2uChatActivity.Myadapter.LeftViewHolder){
                ((u2uChatActivity.Myadapter.LeftViewHolder) holder).content.setText(chatDataArrayList.get(position).getMessage());
                ((u2uChatActivity.Myadapter.LeftViewHolder) holder).name.setText(chatDataArrayList.get(position).getUserID());
                ((u2uChatActivity.Myadapter.LeftViewHolder) holder).image.setImageDrawable(chatDataArrayList.get(position).getIcon());
            }
            else{
                ((u2uChatActivity.Myadapter.RightViewHolder) holder).content.setText(chatDataArrayList.get(position).getMessage());
                ((u2uChatActivity.Myadapter.RightViewHolder) holder).name.setText(chatDataArrayList.get(position).getUserID());
                ((u2uChatActivity.Myadapter.RightViewHolder) holder).image.setImageDrawable(chatDataArrayList.get(position).getIcon());
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
        public static final int Right_contents = 1;
    }
}
