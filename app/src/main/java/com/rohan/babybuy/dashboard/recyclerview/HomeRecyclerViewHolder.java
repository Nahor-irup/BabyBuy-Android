package com.rohan.babybuy.dashboard.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rohan.babybuy.R;

public class HomeRecyclerViewHolder extends RecyclerView.ViewHolder {

    private final ImageView productImage;
    private final TextView productTitle;
//    private final TextView productDescription;
    private final TextView productDate;
//    private final TextView productPrice;
    private ConstraintLayout clRootLayout;

    public HomeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.productImage);
        productTitle = itemView.findViewById(R.id.productTitle);
//        productDescription = itemView.findViewById(R.id.productDescription);
//        productPrice = itemView.findViewById(R.id.productPrice);
        productDate = itemView.findViewById(R.id.productDate);
        clRootLayout = itemView.findViewById(R.id.item_root_layout);
    }

    public ImageView getProductImage() {
        return productImage;
    }

    public TextView getProductTitle() {
        return productTitle;
    }

//    public TextView getProductDescription() {return productDescription;}

    public TextView getProductDate() {return productDate;}

//    public TextView getProductPrice() {return productPrice;}

    public ConstraintLayout getClRootLayout() {return clRootLayout;}
}
