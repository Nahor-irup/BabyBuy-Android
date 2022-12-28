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

import com.google.firebase.auth.FirebaseAuth;
import com.rohan.babybuy.dashboard.DashboardActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView img;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        img=(ImageView) findViewById(R.id.loader);
        firebaseAuth = FirebaseAuth.getInstance();

        loader();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserLogin();
            }
        },2000);

    }

    private void checkUserLogin(){
        Intent intent;
        if(firebaseAuth.getCurrentUser()!=null){
            intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
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