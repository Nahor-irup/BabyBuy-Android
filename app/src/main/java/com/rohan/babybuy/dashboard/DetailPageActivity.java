package com.rohan.babybuy.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
private TextView detailTitle,detailDescription;
private FloatingActionButton btnDelete,btnEdit;
String key = "";
String imageUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        getSupportActionBar().hide();

        detailImage = findViewById(R.id.detailPageImage);
        detailTitle = findViewById(R.id.detailPageTitle);
        detailDescription = findViewById(R.id.detailPageDescription);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);

//        Intent intent = getIntent();
//        Product product = (Product) intent.getSerializableExtra("product_data");
//        if(intent!=null){
//            detailTitle.setText(product.title);
//            detailDescription.setText(product.description);
//            key = getString(Integer.parseInt(product.key));
//            Glide.with(this).load(product.getImages()).into(detailImage);
//        }

        Bundle bundle = getIntent().getExtras();
        Product product = (Product) bundle.getSerializable("product_data");

        if(bundle!=null){
//            detailTitle.setText(bundle.getString("title"));
//            detailDescription.setText(bundle.getString("description"));
//            Glide.with(this).load(bundle.getString("image")).into(detailImage);
            detailTitle.setText(product.getTitle());
            detailDescription.setText(product.description);
            key = product.key;
            imageUrl = product.getImages();
            Glide.with(this).load(imageUrl).into(detailImage);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPageActivity.this,UpdateProductActivity.class)
                        .putExtra("Title",detailTitle.getText().toString())
                        .putExtra("Description",detailDescription.getText().toString())
                        .putExtra("Image",imageUrl)
                        .putExtra("Key",key);
                startActivity(intent);
            }
        });

        //ToDo on back button clicked
        setResult(2002);
//        finish();
    }


    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete?")
                .setIcon(R.drawable.icon_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product");
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child(key).removeValue();
                                Toast.makeText(DetailPageActivity.this, "Product deleted.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
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