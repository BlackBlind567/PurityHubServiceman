package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.UpdateListener;
import com.atoms.purityhubserviceman.databinding.GenerateBillItemLayoutBinding;
import com.atoms.purityhubserviceman.model.GenerateBill;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class GenerateBillAdapter extends BlindAdapter<GenerateBill, GenerateBillItemLayoutBinding>{

    public GenerateBillAdapter(Context mContext, ArrayList<GenerateBill> mArrayList, UpdateListener updatelistner) {
        super(mContext, mArrayList, updatelistner);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.generate_bill_item_layout;
    }

    @Override
    public void onBindData(GenerateBill model, int position, GenerateBillItemLayoutBinding dataBinding) {
        dataBinding.itemPrize.setText("\u20B9" +model.getProductPrice());
        dataBinding.itemTitle.setText(model.getProductName());
        dataBinding.itemQuantity.setText("x "+model.getProductQuantity());
        dataBinding.itemMrp.setText(model.getProductMrp());
        dataBinding.itemMrp.setPaintFlags(dataBinding.itemMrp.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with(getContext()).load(model.getProductImage()).diskCacheStrategy(DiskCacheStrategy.NONE).into(dataBinding.itemImage);
        dataBinding.itemTotalPrize.setText("\u20B9" + String.valueOf(Integer.parseInt(model.getProductPrice()) * Integer.parseInt(model.getProductQuantity())));
        dataBinding.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                removeWithoutItemList(position);
                getUpdatelistner().generatedRemoveBillData(model.getProductId(), model.getProductQuantity(),
                        String.valueOf(Integer.parseInt(model.getProductPrice()) * Integer.parseInt(model.getProductQuantity())), position );
            }
        });
    }

    @Override
    protected void onBindDataHolder(GenerateBill generateBill, int position, GenerateBillItemLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(GenerateBill model, int position) {

    }

    @Override
    public void removeWithoutItemList(int removeItemPosition) {
        super.removeWithoutItemList(removeItemPosition);
    }
}
