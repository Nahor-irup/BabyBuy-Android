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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohan.babybuy.dashboard.DashboardActivity;
import com.rohan.babybuy.db.BabyBuyDatabase;
import com.rohan.babybuy.db.User;


public class RegisterActivity extends AppCompatActivity {
    private TextView log;
    private Button btnRegister;
    private EditText textBoxUsername, textBoxEmail, textBoxpass,txtBoxRePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        log = findViewById(R.id.txtLoginLink);
        btnRegister = findViewById(R.id.btnRegister);
        textBoxUsername = findViewById(R.id.txtBoxUsername);
        textBoxEmail = findViewById(R.id.txtBoxEmail);
        textBoxpass = findViewById(R.id.txtBoxPassword);
        txtBoxRePassword = findViewById(R.id.txtBoxRePassword);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");


        log.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        //Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, email, password,rePassword;
                username = textBoxUsername.getText().toString().trim();
                email = textBoxEmail.getText().toString().trim();
                password = textBoxpass.getText().toString().trim();
                rePassword = txtBoxRePassword.getText().toString().trim();

                if (!username.isEmpty()) {
                    if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (!password.isEmpty()) {
                            if(password.equals(rePassword)){
                                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser verifyUser = firebaseAuth.getCurrentUser();
                                            User user = new User(verifyUser.getUid(),username, email, password,"","");
                                            databaseReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child(verifyUser.getUid()).setValue(user);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(RegisterActivity.this, "Error: " + error + " occurred!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            verifyUser.sendEmailVerification();

                                            Toast.makeText(RegisterActivity.this, "User created successfully. Please check your email to verify.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed!!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            textBoxEmail.findFocus();
                                        }
                                    }
                                });
                            }else{
                                txtBoxRePassword.setError("Confirm password didn't match!!");
                            }
                        } else {
                            textBoxpass.setError("Password cannot be empty!!");
                        }
                    } else if (email.isEmpty()) {
                        textBoxEmail.setError("Email cannot be empty!!");
                    } else {
                        textBoxEmail.setError("Invalid email format!!");
                    }
                } else {
                    textBoxUsername.setError("Username cannot be empty!!");
                }
            }
        });
    }

}