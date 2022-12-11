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

public class LoginActivity extends AppCompatActivity {
    private TextView reg;
    private Button btnLogin;
    private EditText em,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        reg=(TextView) findViewById(R.id.txtRegisterLink);;
        btnLogin=(Button) findViewById(R.id.btnLogin);
        em=(EditText) findViewById(R.id.txtBoxEmail);
        pass=(EditText) findViewById(R.id.txtBoxPassword);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=em.getText().toString().trim();
                String password=pass.getText().toString().trim();


                BabyBuyDatabase babyBuyDatabase = Room.databaseBuilder(getApplicationContext(),
                    BabyBuyDatabase.class,"BabyBuy.db").build();
                UserDao userDao= babyBuyDatabase.getUserDao();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        User user=userDao.getUser(email,password);
                        if(user==null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Invalid Credentials!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(LoginActivity.this, "Login Success.", Toast.LENGTH_SHORT).show();

                                   SharedPreferences sharedPreferences = getSharedPreferences("Login_pref", Context.MODE_PRIVATE);
                                   SharedPreferences.Editor editor = sharedPreferences.edit();
                                   editor.putString("user_name", user.username);
                                   editor.putString("user_email",email);
                                   editor.putString("user_password",password);
                                   editor.putBoolean("is_logged_in",true);
                                   editor.apply();

                                   Intent intent =new Intent(LoginActivity.this,DashboardActivity.class);
                                   startActivity(intent);
                                   finish();
                               }
                           });
                        }
                    }
                }).start();
            }
        });

    }
}