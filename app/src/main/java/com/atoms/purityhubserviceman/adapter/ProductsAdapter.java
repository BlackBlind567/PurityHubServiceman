package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.UpdateListener;
import com.atoms.purityhubserviceman.activity.GenerateBillActivity;
import com.atoms.purityhubserviceman.databinding.ProductsLayoutBinding;
import com.atoms.purityhubserviceman.fragments.ProductItemDetailFragment;
import com.atoms.purityhubserviceman.model.ProductData;
import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class ProductsAdapter extends BlindAdapter<ProductData, ProductsLayoutBinding> {


    public ProductsAdapter(Context context, ArrayList<ProductData> arrayList) {
        super(context, arrayList);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.products_layout;
    }

    @Override
    public void onBindData(ProductData model, int position, ProductsLayoutBinding dataBinding) {
        dataBinding.title.setText(model.getTitle());
        dataBinding.mrp.setText( model.getMrp());
        dataBinding.price.setText( "Price: " + model.getPrice());
        dataBinding.mrp.setPaintFlags(dataBinding.mrp.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with(getContext()).load(model.getImage()).into(dataBinding.shapeIv);
//        dataBinding.title.setText(model.getTitle());

        dataBinding.addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), GenerateBillActivity.class);
//                intent.putExtra("id", String.valueOf(model.getId()));
//                intent.putExtra("title", String.valueOf(model.getTitle()));
//                intent.putExtra("price", String.valueOf(model.getPrice()));
//                getContext().startActivity(intent);
                ProductItemDetailFragment bottomSheet = new ProductItemDetailFragment(model.getId(), model.getTitle(),
                        model.getPrice(), model.getMrp(), model.getImage());
                bottomSheet.show(
                        ((FragmentActivity)mContext).getSupportFragmentManager(),
                        "BrandsFragment"
                );
            }
        });


    }

    @Override
    protected void onBindDataHolder(ProductData productData, int position, ProductsLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(ProductData model, int position) {

    }
}
