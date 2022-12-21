package com.rohan.babybuy.dashboard;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rohan.babybuy.R;
import com.rohan.babybuy.dashboard.recyclerview.HomeRecyclerViewAdapter;
import com.rohan.babybuy.db.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FloatingActionButton btnAdd;
    private RecyclerView homeRecyclerView;
    private ImageView img;
    private AddProductFragment addProductFragment;
    DashboardActivity dashboardActivity;

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
        addProductFragment = AddProductFragment.newInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpHomeRecyclerView();
        if(!getProductList().isEmpty()){
            img.setVisibility(View.INVISIBLE);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireActivity(), "Button clicked", Toast.LENGTH_SHORT).show();
                AddProductFragment addProductFragment = new AddProductFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,addProductFragment).commit();
//                dashboardActivity.replaceFragment(addProductFragment);
            }
        });
    }

    private void setUpHomeRecyclerView(){
        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(requireActivity(),getProductList());
        homeRecyclerView.setAdapter(adapter);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
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
}