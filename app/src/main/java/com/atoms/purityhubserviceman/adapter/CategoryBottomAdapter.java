package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.UpdateListener;
import com.atoms.purityhubserviceman.databinding.BottomItemLayoutBinding;
import com.atoms.purityhubserviceman.databinding.SpinnerLayoutBinding;
import com.atoms.purityhubserviceman.model.BrandCategoryData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryBottomAdapter extends BlindAdapter<BrandCategoryData, BottomItemLayoutBinding> {

    public CategoryBottomAdapter(Context mContext, ArrayList<BrandCategoryData> mArrayList, UpdateListener updatelistner, String valueCheck) {
        super(mContext, mArrayList, updatelistner, valueCheck);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.bottom_item_layout;
    }

    @Override
    public void onBindData(BrandCategoryData model, int position, BottomItemLayoutBinding dataBinding) {
        dataBinding.tv.setText(model.getName());
        Glide.with(getContext()).load(model.getImage()).into(dataBinding.shapeIv);
        dataBinding.radioButton.setVisibility(View.VISIBLE);

        if (Objects.requireNonNull(getValueCheck()).equalsIgnoreCase(model.getName())){
            dataBinding.radioButton.setChecked(true);
        }else {
            dataBinding.radioButton.setChecked(false);
        }
        dataBinding.radioButton.setChecked(getValueCheck().equalsIgnoreCase(model.getName()));



        dataBinding.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBinding.radioButton.setChecked(true);

                getUpdatelistner().selectCategoryName(model.getName(), model.getId());


            }
        });
    }

    @Override
    protected void onBindDataHolder(BrandCategoryData brandCategoryData, int position, BottomItemLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(BrandCategoryData model, int position) {

    }


}
