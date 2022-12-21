package com.rohan.babybuy.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rohan.babybuy.R;
import com.rohan.babybuy.db.BabyBuyDatabase;
import com.rohan.babybuy.db.Product;
import com.rohan.babybuy.db.ProductDao;

public class AddProductFragment extends Fragment {

    private Button btnSave;
    private Button btnCancel;
    private EditText title,description;

    public static AddProductFragment newInstance() {
        AddProductFragment fragment = new AddProductFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        btnSave = view.findViewById(R.id.btnSave);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productTitle,productDescription;
                productTitle = title.getText().toString();
                productDescription = description.getText().toString();

                if(productTitle.isEmpty())
                {
                    Toast.makeText(requireActivity(), "Please enter title!!", Toast.LENGTH_SHORT).show();
                }else{
                    checkProductAndInsert(productTitle,productDescription,0,null);
                }
            }
        });

    }

    private void insertProduct(String title, String description, Integer plId, String img){
        BabyBuyDatabase babyBuyDatabase = Room.databaseBuilder(requireActivity().getApplicationContext(),
                BabyBuyDatabase.class,"BabyBuy.db").build();
        ProductDao productDao = babyBuyDatabase.getProductDao();

        Product product = new Product();
        product.title = title;
        product.description = description;
        product.images = img;
        product.plId = plId;
        productDao.insertProduct(product);
    }

    private void checkProductAndInsert(String title, String description, Integer plId, String img){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BabyBuyDatabase babyBuyDatabase = Room.databaseBuilder(requireActivity().getApplicationContext(),
                        BabyBuyDatabase.class,"BabyBuy.db").build();
                ProductDao productDao = babyBuyDatabase.getProductDao();

                Product product = productDao.productExist(title);

                if(product!=null){
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "Product already exist!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    insertProduct(title,description,plId,img);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireActivity(), "Product added successfully.", Toast.LENGTH_SHORT).show();
                            HomeFragment homeFragment = new HomeFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,homeFragment).commit();
                        }
                    });
                }
            }
        }).start();
    }
}