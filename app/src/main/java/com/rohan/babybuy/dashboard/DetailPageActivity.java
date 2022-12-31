package com.rohan.babybuy.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rohan.babybuy.R;
import com.rohan.babybuy.db.Product;

public class DetailPageActivity extends AppCompatActivity {
    private ImageView detailImage;
    private TextView detailTitle, detailDescription,detailPageDate;
    private FloatingActionButton btnDelete, btnEdit;
    private Button btnViewOnMap;
    String key = "";
    String imageUrl = "";
    String latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        getSupportActionBar().hide();

        detailImage = findViewById(R.id.detailPageImage);
        detailTitle = findViewById(R.id.detailPageTitle);
        detailDescription = findViewById(R.id.detailPageDescription);
        detailPageDate = findViewById(R.id.detailPageDate);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);

        Bundle bundle = getIntent().getExtras();
        Product product = (Product) bundle.getSerializable("product_data");

        if (product != null) {
            detailTitle.setText(product.getTitle());
            detailDescription.setText(product.getDescription());
            detailPageDate.setText(product.getDate().substring(0,10));
            key = product.getKey();
            imageUrl = product.getImages();
            if (!imageUrl.equals("")) {
                Glide.with(this).load(imageUrl).into(detailImage);
            }
            latitude = product.getLatitude().toString();
            longitude = product.getLongitude().toString();
        }

        //button view on map action
        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailPageActivity.this,MapActivity.class);
                Double latitude, longitude;

                if(product!=null){
                    Bundle params = new Bundle();
                    latitude = Double.valueOf(product.getLatitude());
                    longitude = Double.valueOf(product.getLongitude());
                    params.putDouble("latitude", latitude);
                    params.putDouble("longitude", longitude);
                    params.putString("page","product_detail");
                    intent.putExtras(params);
                }
                startActivity(intent);
            }
        });
        //button delete action
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });

        //button edit action
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPageActivity.this, UpdateProductActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDescription.getText().toString())
                        .putExtra("Latitude",latitude)
                        .putExtra("Longitude",longitude)
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });

        //ToDo on back button clicked
        setResult(2002);
//        finish();
    }

    public Intent getFromIntent() {
        Intent intent = getIntent();
        return intent;
    }
    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete?")
                .setIcon(R.drawable.icon_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product");
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        if (!imageUrl.equals("")) {
                            StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                            storageReference.delete();
                        }
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DetailPageActivity.this, "Product deleted.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                finish();
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        return myQuittingDialogBox;
    }
}