package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.UpdateListener;
import com.atoms.purityhubserviceman.activity.GenerateBillActivity;
import com.atoms.purityhubserviceman.activity.ProductsActivity;
import com.atoms.purityhubserviceman.activity.ServiceRequestDetailActivity;
import com.atoms.purityhubserviceman.databinding.ServiceRequestLayoutBinding;
import com.chinalwb.slidetoconfirmlib.ISlideListener;
import com.chinalwb.slidetoconfirmlib.SlideToConfirm;
import com.myapplication.model.ServiceRequestData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServiceRequestAdapter extends BlindAdapter<ServiceRequestData, ServiceRequestLayoutBinding> {

    public ServiceRequestAdapter(Context mContext, ArrayList<ServiceRequestData> mArrayList, UpdateListener updatelistner, String valueCheck) {
        super(mContext, mArrayList, updatelistner, valueCheck);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.service_request_layout;
    }

    @Override
    public void onBindData(ServiceRequestData model, int position, ServiceRequestLayoutBinding dataBinding) {
            dataBinding.srServiceman.setText(model.getUser());
            dataBinding.srBrandCategory.setText(model.getAddress() + ", " + model.getCity() + ", " + model.getState());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(model.getCreated_at());
            String day = (String) DateFormat.format("dd", date); // 20
            String monthString = (String) DateFormat.format("MMM", date); // Jun
            String monthNumber = (String) DateFormat.format("MM", date); // 06
            String year = (String) DateFormat.format("yyyy", date); // 2013

            dataBinding.srDay.setText(day);
            dataBinding.srMonth.setText(monthString);
            dataBinding.srYear.setText(year);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dataBinding.srViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ServiceRequestDetailActivity.class);
                intent.putExtra("servicemanName",model.getServiceman());
                intent.putExtra("brandName",model.getBrand());
                intent.putExtra("categoryName",model.getService_category());
                intent.putExtra("image", (String) model.getImage());
                intent.putExtra("state",model.getState());
                intent.putExtra("city",model.getCity());
                intent.putExtra("address",model.getAddress());
                intent.putExtra("priority",model.getPriority());
                intent.putExtra("problemType",model.getProblem_type());
                intent.putExtra("remark",model.getRemark());
                intent.putExtra("createdAt",model.getCreated_at());
                getContext().startActivity(intent);

            }
        });
        if (valueCheck.equals("Open")){
            dataBinding.btnSlider.setEngageText("Slide to close");
            dataBinding.btnSlider.setCompletedText("Closed");
        }else if (valueCheck.equals("Close")){
            dataBinding.btnSlider.setEngageText("Generate Bill");
            dataBinding.btnSlider.setCompletedText("Generated");
        }else if (valueCheck.equals("Pending")){
            dataBinding.btnSlider.setEngageText("Slide to open");
            dataBinding.btnSlider.setCompletedText("Opened");
        }


        dataBinding.btnSlider.setSlideListener(new ISlideListener() {
            @Override
            public void onSlideStart() {
                System.out.println("slider1");
            }

            @Override
            public void onSlideMove(float percent) {
                System.out.println("slider4 = " + percent);
            }

            @Override
            public void onSlideCancel() {
                System.out.println("slider3");
            }

            @Override
            public void onSlideDone() {
                System.out.println("api call ");
                if (valueCheck.equals("Pending")) {
                    getUpdatelistner().openRequest(String.valueOf(model.getId()), position);
                }else if (valueCheck.equals("Open")) {
                    getUpdatelistner().closeRequest(String.valueOf(model.getId()), position);
                }else if (valueCheck.equals("Close")){
//                    getUpdatelistner().closeRequest(String.valueOf(model.getId()), position);
                    Intent intent = new Intent(getContext(), GenerateBillActivity.class);
                    getContext().startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onBindDataHolder(ServiceRequestData serviceRequestData, int position, ServiceRequestLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(ServiceRequestData model, int position) {

    }

    @Override
    public void removeItem(int removeItemPosition, ArrayList<ServiceRequestData> arrayList) {
        super.removeItem(removeItemPosition, arrayList);
    }
}
