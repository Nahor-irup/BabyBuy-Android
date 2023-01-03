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

public class PurchasedRecyclerViewAdapter extends RecyclerView.Adapter<PurchasedRecyclerViewHolder> {
    private Context context;
    private List<Product> products;
    private PurchasedRecyclerViewAdapter.IPurchasedRecyclerAdapterListener listener;


    public PurchasedRecyclerViewAdapter(Context context, List<Product> products, IPurchasedRecyclerAdapterListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PurchasedRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.purchased_product_layout,parent,false);

        return new PurchasedRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasedRecyclerViewHolder holder, int position) {

        Product product = products.get(position);
//        holder.getProductImage().setImageDrawable(ContextCompat.getDrawable(context,R.drawable.mother_baby));
        if(!product.getImages().equals("")){
            Glide.with(context).load(product.getImages()).into(holder.getIvProduct());
        }
        holder.getTxtTitle().setText(product.title);
//        holder.getTxtDescription().setText(product.description);
//        holder.getProductPrice().setText(product.price);
        holder.getTxtDate().setText(product.getDate());

        product.key = products.get(holder.getAdapterPosition()).getKey();

        holder.getClPurchasedLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Product at "+holder.getAdapterPosition()+" position is clicked", Toast.LENGTH_SHORT).show();
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

    public interface IPurchasedRecyclerAdapterListener{
        void onItemClicked(Product product);
    }
}
