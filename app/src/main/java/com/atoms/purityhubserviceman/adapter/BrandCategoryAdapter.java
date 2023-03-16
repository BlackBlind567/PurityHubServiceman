package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.model.BrandCategoryData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class BrandCategoryAdapter extends ArrayAdapter<BrandCategoryData> {

//    clickItem clickItem;
//    public interface clickItem{
//        void onClickItem(String name);
//    }

    public BrandCategoryAdapter(@NonNull Context context, ArrayList<BrandCategoryData> brandCategoryDataArrayList) {
        super(context, 0, brandCategoryDataArrayList);
//        this.clickItem = clickItem;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // convertView which is recyclable view
        BrandCategoryData brandCategoryData = getItem(position);
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_layout, parent, false);
        }

//        ShapeableImageView shapeableImageView = currentItemView.findViewById(R.id.shape_iv);
//        Glide.with(getContext()).load(brandCategoryData.getImage()).diskCacheStrategy(DiskCacheStrategy.NONE).into(shapeableImageView);
        TextView textView = currentItemView.findViewById(R.id.tv);
        textView.setText(brandCategoryData.getName());

//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickItem.onClickItem(brandCategoryData.getName());
//            }
//        });

        return currentItemView;
    }
}
