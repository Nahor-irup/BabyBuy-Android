package com.rohan.babybuy;

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
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    private Button btnLogOut;
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
                        break;
                    case R.id.purchased:
                        replaceFragment(purchasedFragment);
                        break;
                    case R.id.profile:
                        replaceFragment(profileFragment);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        btnLogOut = (Button) findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("Login_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.remove("user_name");   //remove single value
                editor.clear();     //clear every value
                editor.apply();

                startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view,fragment);
        fragmentTransaction.commit();
    }
}