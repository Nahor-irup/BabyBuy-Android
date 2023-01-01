package com.rohan.babybuy.dashboard;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rohan.babybuy.LoginActivity;
import com.rohan.babybuy.R;
import com.rohan.babybuy.db.Product;
import com.rohan.babybuy.db.User;

import java.text.DateFormat;
import java.util.Calendar;

public class ProfileFragment extends Fragment {
    private Button btnLogOut, btnUpdate;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText txtBoxUsername;
    private TextInputEditText txtBoxUserEmail;
    private TextInputEditText txtBoxUserContact;
    private TextInputEditText txtBoxNewPassword;
    private TextInputEditText txtBoxRePassword;
    private ImageView imgProfile;
    private String oldImageUrl = "";
    private String id, oldPassword, removeOldImg, key;
    private Uri uri;
    private String imageUrl = "";
    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        txtBoxUsername = (TextInputEditText) view.findViewById(R.id.txtBoxProfileUsername);
        txtBoxUserEmail = (TextInputEditText) view.findViewById(R.id.txtBoxProfileEmail);
        txtBoxUserContact = (TextInputEditText) view.findViewById(R.id.txtBoxProfileContact);
        txtBoxNewPassword = (TextInputEditText) view.findViewById(R.id.txtBoxProfileNewPassword);
        txtBoxRePassword = (TextInputEditText) view.findViewById(R.id.txtBoxProfileRePassword);
        imgProfile = view.findViewById(R.id.imgProfile);
        firebaseAuth = FirebaseAuth.getInstance();
        id = firebaseAuth.getCurrentUser().getUid();


        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get data from database
        getData();

        //upload profile image
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(Intent.createChooser(photoPicker, "Please select an image."), 101);
            }
        });

        //btn update action
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    if (!oldImageUrl.equals("")) {
                        updateData();
                    } else {
                        saveData();
                    }
                } else {
                    updateData();
                }
//                getFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
            }
        });

        //btn logout action
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data.getData();
            imgProfile.setImageURI(uri);
            removeOldImg = oldImageUrl;
            oldImageUrl = "";
        }
    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");

        reference.orderByChild("id").equalTo(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setKey(data.getKey());
                    String img = data.child("images").getValue().toString();
                    oldPassword = data.child("password").getValue().toString();
                    if (!img.equals("")) {
                        Glide.with(getContext()).load(img).into(imgProfile);
                        oldImageUrl = img;
                    }
                    txtBoxUsername.setText(data.child("username").getValue().toString());
                    txtBoxUserEmail.setText(data.child("email").getValue().toString());
                    txtBoxUserContact.setText(data.child("contact").getValue().toString());
                    key = data.getKey();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void saveData() {

        storageReference = FirebaseStorage.getInstance().getReference().child("UserImages").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                updateData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void updateData() {
        String username, email, contact, password, rePass;
        String changePassword ="";
        User user;
        username = txtBoxUsername.getText().toString();
        email = txtBoxUserEmail.getText().toString();
        contact = txtBoxUserContact.getText().toString();
        password = txtBoxNewPassword.getText().toString();
        rePass = txtBoxRePassword.getText().toString();

        if (imageUrl.equals("")) {
            imageUrl = oldImageUrl;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        changePassword = password;
        if (!changePassword.equals("")) {
            user = new User(id, username, email, changePassword, imageUrl, contact);
        } else {
            user = new User(id, username, email, oldPassword, imageUrl, contact);
        }

        if (!password.equals("") && !rePass.equals("")) {
            if (!password.equals(rePass)) {
                txtBoxRePassword.setError("Password doesn't match!!");
                dialog.dismiss();
            } else {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                AuthCredential authCredential = EmailAuthProvider.getCredential(email, oldPassword);
                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(requireActivity(), "Something went wrong. Please try again!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        databaseReference.child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (uri != null) {
                                                        if (!removeOldImg.equals("")) {
                                                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                                                            reference.delete();
                                                        }
                                                    }
                                                    Toast.makeText(requireActivity(), "User updated.", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(requireActivity(), "Authentication failed!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent = new Intent(requireActivity(),DashboardActivity.class);
                startActivity(intent);
            }
        } else {
            databaseReference.child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (uri != null) {
                            if (!removeOldImg.equals("")) {
                                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                                reference.delete();
                            }
                        }
                        Toast.makeText(requireActivity(), "User updated.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireActivity(),DashboardActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}