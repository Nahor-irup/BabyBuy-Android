package com.rohan.babybuy.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
    private TextView detailTitle, detailDescription,detailPageDate,detailPagePrice;
    private FloatingActionButton btnDelete, btnEdit,btnShare;
    private Button btnViewOnMap;
    String key = "";
    String imageUrl = "";
    String latitude,longitude;
    String title,description,location,price;
    Boolean isPurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        getSupportActionBar().hide();

        detailImage = findViewById(R.id.detailPageImage);
        detailTitle = findViewById(R.id.detailPageTitle);
        detailDescription = findViewById(R.id.detailPageDescription);
        detailPageDate = findViewById(R.id.detailPageDate);
        detailPagePrice = findViewById(R.id.detailPagePrice);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        btnShare = findViewById(R.id.btnShare);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);

        Bundle bundle = getIntent().getExtras();
        Product product = (Product) bundle.getSerializable("product_data");

        if (product != null) {
            detailTitle.setText(product.getTitle());
            detailDescription.setText(product.getDescription());
            detailPagePrice.setText(product.getPrice());
            detailPageDate.setText(product.getDate().substring(0,11));
            key = product.getKey();
            imageUrl = product.getImages();
            if (!imageUrl.equals("")) {
                Glide.with(this).load(imageUrl).into(detailImage);
            }
            latitude = product.getLatitude().toString();
            longitude = product.getLongitude().toString();
            location = product.getAddress();
            isPurchased = product.getPurchased();
        }
        title = detailTitle.getText().toString();
        description = detailDescription.getText().toString();
        price =detailPagePrice.getText().toString();


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
                        .putExtra("Address",location)
                        .putExtra("Price",price)
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key)
                        .putExtra("isPurchased",isPurchased);
                startActivity(intent);
            }
        });

        //btn share action
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareString = Html.fromHtml(
                        "<u>Product Detail</u><br>Product Name :<b> " + title + "</b><br>" +
                                "Product Description :<b> " + description + "</b><br>" +
                                "Product Price :<b> " + price + "</b><br>" +
                                "Image :<b> " + imageUrl + "</b><br>" +
                                "<>br>You Can find this Product at : " + location + "</p>"
                ).toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Product Detail");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareString);


                if (sharingIntent.resolveActivity(DetailPageActivity.this.getPackageManager()) != null)
                    DetailPageActivity.this.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                else {
                    Toast.makeText(DetailPageActivity.this
                            , "No app found on your phone which can perform this action", Toast.LENGTH_SHORT).show();
                }
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