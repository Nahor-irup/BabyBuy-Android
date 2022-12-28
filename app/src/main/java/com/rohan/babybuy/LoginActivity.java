package com.rohan.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rohan.babybuy.dashboard.DashboardActivity;
import com.rohan.babybuy.db.BabyBuyDatabase;
import com.rohan.babybuy.db.User;
import com.rohan.babybuy.db.UserDao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private TextView reg;
    private Button btnLogin;
    private EditText textBoxEmail,textBoxPass;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        reg=(TextView) findViewById(R.id.txtRegisterLink);;
        btnLogin=(Button) findViewById(R.id.btnLogin);
        textBoxEmail=(EditText) findViewById(R.id.txtBoxEmail);
        textBoxPass=(EditText) findViewById(R.id.txtBoxPassword);
        firebaseAuth = FirebaseAuth.getInstance();

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
                String email=textBoxEmail.getText().toString().trim();
                String password=textBoxPass.getText().toString().trim();

                if(!email.isEmpty()&&Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!password.isEmpty()){
                        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if(user.isEmailVerified()){
                                    Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Please verify your email!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Invalid Credential!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        textBoxPass.setError("Password cannot be empty!!");
                    }
                }else if(email.isEmpty()){
                    textBoxEmail.setError("Email cannot be empty!!");
                }else {
                    textBoxEmail.setError("Invalid email format!!");
                }
            }
        });
    }
}