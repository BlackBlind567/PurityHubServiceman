package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.recyclerview.widget.RecyclerView;

import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.databinding.GenerateBillItemLayoutBinding;
import com.atoms.purityhubserviceman.model.GenerateBill;

import java.util.ArrayList;

public class GenerateBillAdapter extends BlindAdapter<GenerateBill, GenerateBillItemLayoutBinding>{
    public GenerateBillAdapter(Context context, ArrayList<GenerateBill> arrayList) {
        super(context, arrayList);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.generate_bill_item_layout;
    }

    @Override
    public void onBindData(GenerateBill model, int position, GenerateBillItemLayoutBinding dataBinding) {
        dataBinding.itemPrize.setText(model.getProductMrp());
        dataBinding.itemTitle.setText(model.getProductName());
        dataBinding.itemQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onBindDataHolder(GenerateBill generateBill, int position, GenerateBillItemLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(GenerateBill model, int position) {

    }
}
