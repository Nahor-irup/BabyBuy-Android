package com.rohan.babybuy.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rohan.babybuy.R;
import com.rohan.babybuy.dashboard.recyclerview.HomeRecyclerViewAdapter;
import com.rohan.babybuy.dashboard.recyclerview.PurchasedRecyclerViewAdapter;
import com.rohan.babybuy.db.Product;

import java.util.ArrayList;
import java.util.List;

public class PurchasedFragment extends Fragment implements PurchasedRecyclerViewAdapter.IPurchasedRecyclerAdapterListener  {
    private RecyclerView purchasedRecyclerView;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private List<Product> productList = new ArrayList<>();
    private SearchView searchView;
    private PurchasedRecyclerViewAdapter adapter;
    private ImageView img;
    private FirebaseAuth firebaseAuth;



    public static PurchasedFragment newInstance() {
        PurchasedFragment fragment = new PurchasedFragment();
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
        View view = inflater.inflate(R.layout.fragment_purchased, container, false);

        purchasedRecyclerView = view.findViewById(R.id.recycler_view_purchased);
        img = view.findViewById(R.id.imgMom);
        searchView = view.findViewById(R.id.search);
        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView.clearFocus();
        setUpHomeRecyclerView();

    }


    private void setUpHomeRecyclerView() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 1);
        purchasedRecyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        adapter = new PurchasedRecyclerViewAdapter(requireActivity(), productList, this);
        purchasedRecyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("product");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Product product = itemSnapshot.getValue(Product.class);
                    product.setKey(itemSnapshot.getKey());
                    if(product.isPurchased==true){
                        productList.add(product);
                        adapter.notifyDataSetChanged();
                    }
                }
                if (!productList.isEmpty()) {
                    img.setVisibility(View.INVISIBLE);
                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            //add product
            if (resultCode == 2001) {
                //add success
//                Toast.makeText(requireActivity(), "success 2001", Toast.LENGTH_SHORT).show();
            } else {
                //add failed
//                Toast.makeText(requireActivity(), "failed 2001", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1002) {
            //detail page
            if (resultCode == 2002) {
//                Toast.makeText(requireActivity(), "202 image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClicked(Product product) {
        Intent intent = new Intent(requireActivity(), DetailPageActivity.class);
        intent.putExtra("product_data", product);
        startActivityForResult(intent, 1008);
    }

    public void searchList(String text) {
        ArrayList<Product> searchList = new ArrayList<>();
        for (Product product : productList) {
            if (product.title.toLowerCase().contains(text.toLowerCase())) {
                searchList.add(product);
            }
        }
        adapter.searchDataList(searchList);
    }
}