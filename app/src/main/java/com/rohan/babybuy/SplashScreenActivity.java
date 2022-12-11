package com.rohan.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        img=(ImageView) findViewById(R.id.loader);

        loader();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserLogin();
            }
        },2000);

    }

    private void checkUserLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("Login_pref", Context.MODE_PRIVATE);
        boolean defaultValue = false;
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in",defaultValue);

        Intent intent;
        if(isLoggedIn){
            intent = new Intent(SplashScreenActivity.this,DashboardActivity.class);
        }else {
            intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void loader(){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim);
        img.startAnimation(animation);
    }
}