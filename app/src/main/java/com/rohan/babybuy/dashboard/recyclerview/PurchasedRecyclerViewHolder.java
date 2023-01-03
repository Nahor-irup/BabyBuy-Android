package com.rohan.babybuy.dashboard.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rohan.babybuy.R;

public class PurchasedRecyclerViewHolder extends RecyclerView.ViewHolder {
    private final ImageView ivProduct;
    private final TextView txtTitle;
//    private final TextView txtDescription;
    private final TextView txtDate;
//    private final TextView productPrice;
    private final ConstraintLayout clPurchasedLayout;

    public PurchasedRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ivProduct = itemView.findViewById(R.id.purchasedImage);
        txtDate = itemView.findViewById(R.id.purchasedDate);
//        productPrice = itemView.findViewById(R.id.productPrice);
        txtTitle = itemView.findViewById(R.id.purchasedTitle);
//        txtDescription = itemView.findViewById(R.id.purchasedDescription);
        clPurchasedLayout = itemView.findViewById(R.id.item_purchased_layout);
    }

    public ImageView getIvProduct() {
        return ivProduct;
    }

    public TextView getTxtTitle() {
        return txtTitle;
    }

//    public TextView getTxtDescription() {
//        return txtDescription;
//    }

    public TextView getTxtDate() {
        return txtDate;
    }

//    public TextView getProductPrice() {return productPrice;}

    public ConstraintLayout getClPurchasedLayout() {return clPurchasedLayout;}
}
