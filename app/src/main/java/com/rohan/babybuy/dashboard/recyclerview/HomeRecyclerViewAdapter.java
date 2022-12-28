package com.rohan.babybuy.dashboard.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rohan.babybuy.R;
import com.rohan.babybuy.db.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewHolder> {
    private Context context;
    private List<Product> products;
    private IHomeRecyclerAdapterListener listener;

    public HomeRecyclerViewAdapter(Context context, List<Product> products, IHomeRecyclerAdapterListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.item_product_layout,parent,false);
        return new HomeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewHolder holder, int position) {


        Product product = products.get(position);
//        holder.getProductImage().setImageDrawable(ContextCompat.getDrawable(context,R.drawable.mother_baby));
        Glide.with(context).load(product.getImages()).into(holder.getProductImage());
        holder.getProductTitle().setText(product.title);
        holder.getProductDescription().setText(product.description);

        product.key = products.get(holder.getAdapterPosition()).getKey();

        holder.getClRootLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Product at "+holder.getAdapterPosition()+" position is clicked", Toast.LENGTH_SHORT).show();
                listener.onItemClicked(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void searchDataList(ArrayList<Product> searchList){
        products = searchList;
        notifyDataSetChanged();
    }

    public interface IHomeRecyclerAdapterListener{
        void onItemClicked(Product product);
    }
}
