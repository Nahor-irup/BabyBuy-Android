package com.rohan.babybuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.babybuy.db.BabyBuyDatabase;
import com.rohan.babybuy.db.User;
import com.rohan.babybuy.db.UserDao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {
private TextView log;
private Button btnRegister;
private EditText user,em,pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        log=(TextView) findViewById(R.id.txtLoginLink);
        btnRegister=(Button) findViewById(R.id.btnRegister);
        user=(EditText) findViewById(R.id.txtBoxUsername);
        em=(EditText) findViewById(R.id.txtBoxEmail);
        pass=(EditText) findViewById(R.id.txtBoxPassword);

        log.setOnClickListener(view -> {
            Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

        //Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username,email,password,hashPassword;
                username=user.getText().toString().trim();
                email=em.getText().toString().trim();
                password=pass.getText().toString().trim();
                hashPassword=md5(password);

                if(username.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter Username!!", Toast.LENGTH_SHORT).show();
                }else if(email.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter Email!!", Toast.LENGTH_SHORT).show();
                }else if(!email.matches("^(.+)@(.+)$")){
                    Toast.makeText(RegisterActivity.this, "Invalid Email Format!!", Toast.LENGTH_SHORT).show();
                }else if(password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter Password!!", Toast.LENGTH_SHORT).show();
                }else{
                    //check user and insert into db
                        userExist(username,email,hashPassword);
                }
            }
        });
    }

    private void insertUserInDB(String username,String email, String password){
        BabyBuyDatabase babyBuyDatabase = Room.databaseBuilder(getApplicationContext(),
                BabyBuyDatabase.class,"BabyBuy.db").build();
        UserDao userDao= babyBuyDatabase.getUserDao();
        User userInfo = new User();
        userInfo.username=username;
        userInfo.email=email;
        userInfo.password=password;
        userDao.insertUser(userInfo);
    }
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void userExist(String username, String email, String hashpassword){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BabyBuyDatabase babyBuyDatabase = Room.databaseBuilder(getApplicationContext(),
                        BabyBuyDatabase.class,"BabyBuy.db").build();
                UserDao userDao = babyBuyDatabase.getUserDao();

                User user = userDao.userExist(email);

                if(user==null){
                    //insert into db
                    insertUserInDB(username,email,hashpassword);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "User created successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "User already exist!!", Toast.LENGTH_SHORT).show();
                            em.findFocus();
                        }
                    });
                }
            }
        }).start();
    }

}