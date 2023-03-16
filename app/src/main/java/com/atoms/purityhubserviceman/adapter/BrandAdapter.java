package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.UpdateListener;
import com.atoms.purityhubserviceman.databinding.SpinnerLayoutBinding;
import com.atoms.purityhubserviceman.model.BrandCategoryData;
import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class BrandAdapter extends BlindAdapter<BrandCategoryData, SpinnerLayoutBinding> {

    public BrandAdapter(Context mContext, ArrayList<BrandCategoryData> mArrayList, UpdateListener updatelistner, String valueCheck) {
        super(mContext, mArrayList, updatelistner, valueCheck);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.spinner_layout;
    }

    @Override
    public void onBindData(BrandCategoryData model, int position, SpinnerLayoutBinding dataBinding) {

        dataBinding.checkButton.setVisibility(View.VISIBLE);

        dataBinding.checkButton.setText(model.getName());

        dataBinding.checkButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    getUpdatelistner().selectBrandName("true", model.getId());
                }else {
                    getUpdatelistner().selectBrandName("false", model.getId());
                }
            }
        });
    }

    @Override
    protected void onBindDataHolder(BrandCategoryData brandCategoryData, int position, SpinnerLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(BrandCategoryData model, int position) {

    }

}
