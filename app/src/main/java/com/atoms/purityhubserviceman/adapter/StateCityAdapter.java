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
import com.atoms.purityhubserviceman.model.StateCityData;


import java.util.ArrayList;

public class StateCityAdapter extends ArrayAdapter<StateCityData> {

//    clickItem clickItem;
//    public interface clickItem{
//        void onClickItem(String name);
//    }

    public StateCityAdapter(@NonNull Context context, ArrayList<StateCityData> stateCityDataArrayList) {
        super(context, 0, stateCityDataArrayList);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // convertView which is recyclable view
        StateCityData stateCityData = getItem(position);
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.state_city_layout, parent, false);
        }

//        ShapeableImageView shapeableImageView = currentItemView.findViewById(R.id.shape_iv);
//        Glide.with(getContext()).load(brandCategoryData.getImage()).diskCacheStrategy(DiskCacheStrategy.NONE).into(shapeableImageView);
        TextView textView = currentItemView.findViewById(R.id.tv);
        textView.setText(stateCityData.getName());

//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickItem.onClickItem(brandCategoryData.getName());
//            }
//        });

        return currentItemView;
    }
}

