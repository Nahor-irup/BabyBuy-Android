package com.rohan.babybuy.dashboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private Button btnSave,btnCancel,btnLocation;
    private ImageView uploadImage;
    private EditText txtBoxTitle,txtBoxDescription,txtBoxLocation;
    private Uri uri;
    String imageUrl;
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
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product");

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        }else{
                            Toast.makeText(AddProductActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                setResult(2001);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        txtBoxLocation.setText(getFromIntent().getStringExtra("address"));

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductActivity.this,MapActivity.class);
               if(getFromIntent().getExtras()!=null){
//                   intent.putExtra("latitude",getFromIntent().getStringExtra("location"));
//                   intent.putExtra("longitude",getFromIntent().getStringExtra("longitude"));
//                   CharSequence s2 = getFromIntent().getStringExtra("address");
                   Bundle params = new Bundle();
                   Double l,lt;

                   l = Double.valueOf(getFromIntent().getStringExtra("latitude"));
                   lt = Double.valueOf(getFromIntent().getStringExtra("longitude"));
                    params.putDouble("latitude",l);
                    params.putDouble("longitude",lt);
                   intent.putExtras(params);
               }
                startActivity(intent);
            }
        });

    }

    public Intent getFromIntent(){
        Intent intent = getIntent();
        return intent;
    }

    public void saveData(){
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
                while (!uriTask.isComplete());
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
    public void uploadData(){
        String title = txtBoxTitle.getText().toString();
        String desc = txtBoxDescription.getText().toString();
        Integer plId = 1;
        Boolean isPurchased = false;
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        Product product = new Product(title,desc,imageUrl,plId,isPurchased);

        databaseReference.child(currentDate).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
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