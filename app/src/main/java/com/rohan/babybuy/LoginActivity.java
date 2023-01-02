package com.rohan.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rohan.babybuy.dashboard.DashboardActivity;
import com.rohan.babybuy.dashboard.GetStarted.GetStartedActivity;
import com.rohan.babybuy.db.BabyBuyDatabase;
import com.rohan.babybuy.db.User;
import com.rohan.babybuy.db.UserDao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private TextView reg,txtForgetPassword;
    private Button btnLogin;
    private EditText textBoxEmail,textBoxPass;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        txtForgetPassword=(TextView) findViewById(R.id.txtForgetPassword);;
        reg=(TextView) findViewById(R.id.txtRegisterLink);;
        btnLogin=(Button) findViewById(R.id.btnLogin);
        textBoxEmail=(EditText) findViewById(R.id.txtBoxEmail);
        textBoxPass=(EditText) findViewById(R.id.txtBoxPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        //register button action
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //button login action
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
                                    Intent intent;
                                    Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                    if(!viewWalkthrough()){
                                        intent = new Intent(LoginActivity.this, GetStartedActivity.class);
                                    }else{
                                        intent = new Intent(LoginActivity.this,DashboardActivity.class);
                                    }
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

        //forget pass link
        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dailog_forget, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(LoginActivity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

    public Boolean viewWalkthrough(){
        SharedPreferences sharedPreferences = getSharedPreferences("walkthrough",Context.MODE_PRIVATE);
        Boolean viewWalkedthrough = sharedPreferences.getBoolean("viewed",false);

        return viewWalkedthrough;
    }

}