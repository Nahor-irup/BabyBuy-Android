package com.rohan.babybuy.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.rohan.babybuy.LoginActivity;
import com.rohan.babybuy.R;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private PurchasedFragment purchasedFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        homeFragment =HomeFragment.newInstance();
        purchasedFragment = PurchasedFragment.newInstance();
        profileFragment  = ProfileFragment.newInstance();
        replaceFragment(homeFragment);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        replaceFragment(homeFragment);
                        return true;
//                        break;
                    case R.id.purchased:
                        replaceFragment(purchasedFragment);
                        return true;
//                        break;
                    case R.id.profile:
                        replaceFragment(profileFragment);
                        return true;
//                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view,fragment);
        fragmentTransaction.commit();
    }
}