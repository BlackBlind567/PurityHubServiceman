package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.databinding.GenerateBillItemLayoutBinding;
import com.atoms.purityhubserviceman.model.ViewBillData;

import java.util.ArrayList;

public class VIewBillAdapter extends BlindAdapter<ViewBillData, GenerateBillItemLayoutBinding>{
    public VIewBillAdapter(Context context, ArrayList<ViewBillData> arrayList) {
        super(context, arrayList);
    }

    @Override
    public int getLayoutResId() {
        return  R.layout.generate_bill_item_layout;
    }

    @Override
    public void onBindData(ViewBillData model, int position, GenerateBillItemLayoutBinding dataBinding) {
        dataBinding.itemPrize.setText("\u20B9" +model.getPrice());
        dataBinding.itemTitle.setText(model.getProduct_name());
        dataBinding.itemQuantity.setText(model.getQuantity());
        dataBinding.itemTotalPrize.setText("\u20B9" + model.getTotal_amount());
        dataBinding.deleteItem.setVisibility(View.GONE);
    }

    @Override
    protected void onBindDataHolder(ViewBillData viewBillData, int position, GenerateBillItemLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(ViewBillData model, int position) {

    }
}
