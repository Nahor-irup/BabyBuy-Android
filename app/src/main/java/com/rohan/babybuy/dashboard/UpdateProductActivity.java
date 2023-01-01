package com.rohan.babybuy.dashboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rohan.babybuy.R;
import com.rohan.babybuy.db.Product;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateProductActivity extends AppCompatActivity {
    private ImageView updateImage;
    private Button btnUpdate,btnCancel,btnLocation,btnShare;
    private EditText updateTitle, updateDesc,txtBoxUpdateLocation,txtBoxUpdatePrice;
    private TextView txtLatitude,txtLongitude;
    private String title, desc;
    private String key,oldImageUrl,removeOldImg;
    private String imageUrl = "";
    private Uri uri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private CheckBox cbUpdateIsPurchased;
    private Boolean isPurchased;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        getSupportActionBar().hide();

        updateImage = findViewById(R.id.imgUpdate);
        updateTitle = findViewById(R.id.txtBoxUpdateTitle);
        updateDesc = findViewById(R.id.txtBoxUpdateDescription);
        txtBoxUpdateLocation = findViewById(R.id.txtBoxUpdateLocation);
        txtBoxUpdatePrice = findViewById(R.id.txtBoxUpdatePrice);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        btnLocation = findViewById(R.id.btnLocation);
        txtLatitude = findViewById(R.id.txtUpdateLatitude);
        txtLongitude = findViewById(R.id.txtUpdateLongitude);
        cbUpdateIsPurchased = findViewById(R.id.cbUpdateIsPurchased);
        firebaseAuth = FirebaseAuth.getInstance();


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(!bundle.getString("Image").equals("")){
                Glide.with(UpdateProductActivity.this).load(bundle.getString("Image")).into(updateImage);
            }
            updateTitle.setText(bundle.getString("Title"));
            updateDesc.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            oldImageUrl = bundle.getString("Image");
            txtBoxUpdateLocation.setText(bundle.getString("Address"));
            txtBoxUpdatePrice.setText(bundle.getString("Price"));
            txtLatitude.setText(bundle.getString("Latitude"));
            txtLongitude.setText(bundle.getString("Longitude"));
            cbUpdateIsPurchased.setChecked(bundle.getBoolean("isPurchased"));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("product").child(key);

        //image uploader action
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(Intent.createChooser(photoPicker,"Please select an image."),101);
            }
        });

        //button update action
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri!=null) {
                    if(!oldImageUrl.equals(""))
                    {
                        updateData();
                    }else{
                        saveData();
                    }
                }else{
                    updateData();
                }

                Intent intent = new Intent(UpdateProductActivity.this,DashboardActivity.class);
                startActivity(intent);
            }
        });

        //btn location action
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProductActivity.this,MapActivity.class);
                Double latitude, longitude;
                if(bundle!=null){
                    Bundle params = new Bundle();
                    latitude = Double.valueOf(bundle.getString("Latitude"));
                    longitude = Double.valueOf(bundle.getString("Longitude"));
                    params.putDouble("latitude", latitude);
                    params.putDouble("longitude", longitude);
                    params.putString("page","product_update");
                    intent.putExtras(params);
                }
                startActivityForResult(intent,301);
            }
        });

        //btn cancel action
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101&& resultCode ==RESULT_OK) {
            uri = data.getData();
            updateImage.setImageURI(uri);
            removeOldImg = oldImageUrl;
            oldImageUrl = "";
        }else if(requestCode==301 && resultCode==202){
            txtBoxUpdateLocation.setText(data.getStringExtra("address"));
            txtLatitude.setText(data.getStringExtra("latitude"));
            txtLongitude.setText(data.getStringExtra("longitude"));
        }
    }

    public void saveData(){

        storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
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

    public void updateData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        String address,price,currentDate,lat,lon;
        Double latitude,longitude;
        title = updateTitle.getText().toString();
        desc = updateDesc.getText().toString();
        lat = txtLatitude.getText().toString();
        lon = txtLongitude.getText().toString();
        if(!lat.equals("")&&!lon.equals("")){
            latitude = Double.valueOf(lat);
            longitude = Double.valueOf(lon);
        }else{
            latitude = 0.0;
            longitude = 0.0;
        }
        address = txtBoxUpdateLocation.getText().toString();
        price = txtBoxUpdatePrice.getText().toString();
        isPurchased = cbUpdateIsPurchased.isChecked();
        currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        if(imageUrl.equals("")){
            imageUrl=oldImageUrl;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        Product product = new Product(user.getUid(),title, desc, imageUrl,latitude,longitude ,address,price, isPurchased,currentDate);

        databaseReference.setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(uri!=null){
                        if(!removeOldImg.equals("")){
                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                            reference.delete();
                        }
                    }
                    Toast.makeText(UpdateProductActivity.this, "Product updated.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProductActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}