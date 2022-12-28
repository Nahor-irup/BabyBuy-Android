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
    private EditText textBoxUsername, textBoxEmail, textBoxpass;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        log = (TextView) findViewById(R.id.txtLoginLink);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        textBoxUsername = (EditText) findViewById(R.id.txtBoxUsername);
        textBoxEmail = (EditText) findViewById(R.id.txtBoxEmail);
        textBoxpass = (EditText) findViewById(R.id.txtBoxPassword);
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
                String username, email, password, hashPassword;
                username = textBoxUsername.getText().toString().trim();
                email = textBoxEmail.getText().toString().trim();
                password = textBoxpass.getText().toString().trim();
                userId = username;

                if (!username.isEmpty()) {
                    if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (!password.isEmpty()) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        User user = new User(username, email, password);
                                        databaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                databaseReference.child(userId).setValue(user);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(RegisterActivity.this, "Error: " + error + " occurred!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        FirebaseUser verifyUser = firebaseAuth.getCurrentUser();
                                        verifyUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this, "User created successfully.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed!!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        textBoxEmail.findFocus();
                                    }
                                }
                            });
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