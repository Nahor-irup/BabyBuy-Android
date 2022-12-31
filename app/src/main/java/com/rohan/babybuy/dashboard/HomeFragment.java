package com.rohan.babybuy.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohan.babybuy.LoginActivity;
import com.rohan.babybuy.R;
import com.rohan.babybuy.dashboard.recyclerview.HomeRecyclerViewAdapter;
import com.rohan.babybuy.db.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements HomeRecyclerViewAdapter.IHomeRecyclerAdapterListener {

    private FloatingActionButton btnAdd;
    private RecyclerView homeRecyclerView;
    private ImageView img;
    private TextView txtUsername;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private List<Product> productList = new ArrayList<>();
    private SearchView searchView;
    private HomeRecyclerViewAdapter adapter;
    private FirebaseAuth firebaseAuth;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAdd = view.findViewById(R.id.btnAdd);
        homeRecyclerView = view.findViewById(R.id.recycler_view_home);
        img = view.findViewById(R.id.imgMom);
        txtUsername = view.findViewById(R.id.txtUsername);
        searchView = view.findViewById(R.id.search);
        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView.clearFocus();
//        getUsername();
        setUpHomeRecyclerView();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddProductActivity();
            }
        });
    }

    private void getUsername(){
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login_pref",Context.MODE_PRIVATE);
//        txtUsername.setText(sharedPreferences.getString("user_name",""));
        String username = firebaseAuth.getCurrentUser().getUid();
        txtUsername.setText(username);
    }

    private void setUpHomeRecyclerView(){

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(),1);
        homeRecyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        adapter = new HomeRecyclerViewAdapter(requireActivity(),productList,this);
        homeRecyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("product");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Product product = itemSnapshot.getValue(Product.class);
                    product.setKey(itemSnapshot.getKey());
                    productList.add(product);
                }
                if(!productList.isEmpty()){
                    img.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    private List<Product> getProductList(){
        List<Product> products = new ArrayList<>();

        Product product1 = new Product();
        product1.title = "Product1";
        product1.description = "lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem ";
        products.add(product1);

        Product product2 = new Product();
        product2.title = "Product2";
        product2.description = "lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem ";
        products.add(product2);

        Product product3 = new Product();
        product3.title = "Product3";
        product3.description = "lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem ";
        products.add(product3);

        Product product4 = new Product();
        product4.title = "Product4";
        product4.description = "lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem ";
        products.add(product4);

        Product product5 = new Product();
        product5.title = "Product5";
        product5.description = "lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem lorem ";
        products.add(product5);

        return products;
    }

    private void startAddProductActivity(){
        Intent intent = new Intent(requireActivity(), AddProductActivity.class);
        startActivityForResult(intent,1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1001){
            //add product
            if(resultCode==2001){
                //add success
//                Toast.makeText(requireActivity(), "success 2001", Toast.LENGTH_SHORT).show();
            }else{
                //add failed
//                Toast.makeText(requireActivity(), "failed 2001", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==1002){
            //detail page
            if(resultCode==2002){
//                Toast.makeText(requireActivity(), "202 image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClicked(Product product) {
        Intent intent = new Intent(requireActivity(),DetailPageActivity.class);
        intent.putExtra("product_data",product);
        startActivityForResult(intent,1002);
    }

    public void searchList(String text){
        ArrayList<Product> searchList = new ArrayList<>();
        for(Product product:productList){
            if(product.title.toLowerCase().contains(text.toLowerCase())){
                searchList.add(product);
            }
        }
        adapter.searchDataList(searchList);
    }
}