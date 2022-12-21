package com.rohan.babybuy.dashboard.recyclerview;

import android.media.Image;
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
    private final TextView productDescription;
    private ConstraintLayout clRootLayout;

    public HomeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.productImage);
        productTitle = itemView.findViewById(R.id.productTitle);
        productDescription = itemView.findViewById(R.id.productDescription);
        clRootLayout = itemView.findViewById(R.id.item_root_layout);
    }

    public ImageView getProductImage() {
        return productImage;
    }

    public TextView getProductTitle() {
        return productTitle;
    }

    public TextView getProductDescription() {
        return productDescription;
    }

    public ConstraintLayout getClRootLayout() {
        return clRootLayout;
    }
}
