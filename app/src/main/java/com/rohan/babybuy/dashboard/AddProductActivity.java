package com.rohan.babybuy.dashboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rohan.babybuy.R;
import com.rohan.babybuy.db.Product;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddProductActivity extends AppCompatActivity {
    private Button btnSave, btnCancel, btnLocation;
    private ImageView uploadImage;
    private EditText txtBoxTitle, txtBoxDescription, txtBoxLocation;
    private TextView txtLatitude, txtLongitude;
    private Uri uri;
    private String imageUrl = "";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_product);
        getSupportActionBar().hide();

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnLocation = findViewById(R.id.btnLocation);
        uploadImage = findViewById(R.id.imgUpload);
        txtBoxTitle = findViewById(R.id.txtBoxTitle);
        txtBoxDescription = findViewById(R.id.txtBoxDescription);
        txtBoxLocation = findViewById(R.id.txtBoxLocation);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product");


        //image uploader
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
                startActivityForResult(Intent.createChooser(photoPicker, "Please select an image."), 101);
            }
        });

//        loadFromSharedPref();

        //save button action
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri == null) {
                    uploadData();
                } else {
                    saveData();
                }
//                clearSharedPref();
                setResult(2001);
            }
        });

        //cancel button action
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //location button action
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductActivity.this, MapActivity.class);

                Double latitude, longitude;
                String lat, lon;
                lat = txtLatitude.getText().toString();
                lon = txtLongitude.getText().toString();
                if (!lat.equals("") && !lon.equals("")) {
                    latitude = Double.valueOf(lat);
                    longitude = Double.valueOf(lon);

                    Bundle params = new Bundle();
                    params.putDouble("latitude", latitude);
                    params.putDouble("longitude", longitude);
//                    params.putString("page","product_add");
                    intent.putExtras(params);
                }

                startActivityForResult(intent, 102);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
//            Intent data = result.getData();
            uri = data.getData();
            uploadImage.setImageURI(uri);
        } else if (requestCode == 102 && resultCode == 202) {
            //set latitude, longitude and address text box
            txtBoxLocation.setText(data.getStringExtra("address"));
            txtLatitude.setText(data.getStringExtra("latitude"));
            txtLongitude.setText(data.getStringExtra("longitude"));
        }
    }

    public Intent getFromIntent() {
        Intent intent = getIntent();
        return intent;
    }

    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
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
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData() {
        String title, desc, address, currentDate,lat,lon;
        Double latitude, longitude;
        title = txtBoxTitle.getText().toString();
        desc = txtBoxDescription.getText().toString();
        address = txtBoxLocation.getText().toString();
        Boolean isPurchased = false;
        currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        lat = txtLatitude.getText().toString();
        lon = txtLongitude.getText().toString();
        if(!lat.equals("")&&!lon.equals("")){
            latitude = Double.valueOf(lat);
            longitude = Double.valueOf(lon);
        }else{
            latitude = 0.0;
            longitude = 0.0;
        }


        Product product = new Product(title, desc, imageUrl, latitude, longitude, address, isPurchased, currentDate);

        databaseReference.child(currentDate).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Product saved successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProductActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}