package com.example.fb_chatapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class SignActivity extends AppCompatActivity {

    static final int KEY_SIZE = 2048;

    EditText Name_Sign;
    EditText ID_Sign;
    EditText Password_Sign;
    Button signButton;
    DatabaseReference DBRef;

    HashMap<String, String> RSAKeypair;

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = Name_Sign.getText().toString();
                String ID = ID_Sign.getText().toString();
                String Password = Password_Sign.getText().toString();

                if(name.length() == 0 || ID.length()==0 || Password.length() ==0){
                    Toast.makeText(SignActivity.this, "Invalid parameters", Toast.LENGTH_SHORT).show();
                }
                else{
                    RSAKeypair = getKeypair();
                    String Publickey = RSAKeypair.get("PublicKey");
                    String Privatekey = RSAKeypair.get("PrivateKey");

                    User user = new User(name,ID,Password, Publickey, Privatekey);
                    DBRef.child("users").child(ID).setValue(user);
                    Toast.makeText(SignActivity.this, "회원가입이 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static HashMap<String, String> getKeypair(){
        HashMap<String, String > stringKeypair = new HashMap<>();

        SecureRandom random = new SecureRandom();
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGen.initialize(1024, random);

        KeyPair keyPair = keyPairGen.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // base64 라이브러리에서 encodeTOString을 이용해서 byte[] 형식을 String 형식으로 변환함.
        String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        System.out.println(stringPrivateKey);
        System.out.println(stringPublicKey);

        stringKeypair.put("PublicKey", stringPublicKey);
        stringKeypair.put("PrivateKey", stringPrivateKey);

        return stringKeypair;
    }

    public void initView(){
        Name_Sign = findViewById(R.id.Name_Sign);
        ID_Sign = findViewById(R.id.ID_SIgn);
        Password_Sign = findViewById(R.id.Password_Sign);
        signButton = findViewById(R.id.signButton_Sign);
    }
}
