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

public class CategoryAdapter extends BlindAdapter<BrandCategoryData, SpinnerLayoutBinding> {

    public CategoryAdapter(Context mContext, ArrayList<BrandCategoryData> mArrayList, UpdateListener updatelistner, String valueCheck) {
        super(mContext, mArrayList, updatelistner, valueCheck);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.spinner_layout;
    }

    @Override
    public void onBindData(BrandCategoryData model, int position, SpinnerLayoutBinding dataBinding) {
//        dataBinding.tv.setText(model.getName());
//        Glide.with(getContext()).load(model.getImage()).into(dataBinding.shapeIv);
        dataBinding.checkButton.setVisibility(View.VISIBLE);

//        if (valueCheck.equalsIgnoreCase(model.getName())){
//            dataBinding.radioButton.setChecked(true);
//        }else {
//            dataBinding.radioButton.setChecked(false);
//        }
//        dataBinding.radioButton.setChecked(valueCheck.equalsIgnoreCase(model.getName()));

        dataBinding.checkButton.setText(model.getName());

        dataBinding.checkButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    getUpdatelistner().selectCategoryName("true", model.getId());
                }else {
                    getUpdatelistner().selectCategoryName("false", model.getId());
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
