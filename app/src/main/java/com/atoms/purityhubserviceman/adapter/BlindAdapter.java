package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


import com.atoms.purityhubserviceman.UpdateListener;

import java.util.ArrayList;
import java.util.Collections;

public abstract class BlindAdapter <T, D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    private ArrayList<T> mArrayList;
    private UpdateListener updatelistner;
    public String valueCheck;

    public BlindAdapter(Context context, ArrayList<T> arrayList) {
        this.mContext = context;
        this.mArrayList = arrayList;
    }

    public BlindAdapter(Context mContext, ArrayList<T> mArrayList, UpdateListener updatelistner) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.updatelistner = updatelistner;
    }

    public BlindAdapter(Context mContext, ArrayList<T> mArrayList, UpdateListener updatelistner, String valueCheck) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.updatelistner = updatelistner;
        this.valueCheck = valueCheck;
    }

    public BlindAdapter(Context mContext, ArrayList<T> mArrayList, String valueCheck) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.valueCheck = valueCheck;
    }

    public abstract int getLayoutResId();

    public abstract void onBindData(T model, int position, D dataBinding);

    protected abstract void onBindDataHolder(T t, int position, D mDataBinding, RecyclerView.ViewHolder holder);

    public abstract void onItemClick(T model, int position);

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutResId(), parent, false);
        RecyclerView.ViewHolder holder = new ItemViewHolder(dataBinding);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        onBindData(mArrayList.get(position), position, ((ItemViewHolder) holder).mDataBinding);
        onBindDataHolder(mArrayList.get(position), position, ((ItemViewHolder) holder).mDataBinding, holder);
        ((ViewDataBinding) ((ItemViewHolder) holder).mDataBinding).getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(mArrayList.get(position), position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void addItems(ArrayList<T> arrayList) {
        mArrayList = arrayList;
        this.notifyDataSetChanged();
    }

    public void swapeItem(int fromPosition, int toPosition) {
        Collections.swap(mArrayList, fromPosition, toPosition);
        this.notifyItemMoved(fromPosition, toPosition);
    }

    public void removeItem(int removeItemPosition,ArrayList<T> arrayList){
        mArrayList = arrayList;
        mArrayList.remove(removeItemPosition);
        notifyItemRemoved(removeItemPosition);
        notifyItemRangeChanged(removeItemPosition, mArrayList.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public T getItem(int position) {
        return mArrayList.get(position);
    }

    public Context getContext() {
        return mContext;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        protected D mDataBinding;

        public ItemViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mDataBinding = (D) binding;
        }
    }

    public UpdateListener getUpdatelistner() {
        return updatelistner;
    }
}

