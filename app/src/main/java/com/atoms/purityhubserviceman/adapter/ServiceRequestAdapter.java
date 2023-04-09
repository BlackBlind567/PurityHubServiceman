package com.atoms.purityhubserviceman.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.UpdateListener;
import com.atoms.purityhubserviceman.activity.GenerateBillActivity;
import com.atoms.purityhubserviceman.activity.ServiceRequestDetailActivity;
import com.atoms.purityhubserviceman.activity.ViewBillActivity;
import com.atoms.purityhubserviceman.databinding.ServiceRequestLayoutBinding;
import com.chinalwb.slidetoconfirmlib.ISlideListener;
import com.atoms.purityhubserviceman.model.ServiceRequestData;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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

        dataBinding.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valueCheck.equals("Open")) {
                    getUpdatelistner().makeCall(model.getUser_mobile());
                }
            }
        });

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
                intent.putExtra("userHouse",model.getUser());
                intent.putExtra("open_datetime",model.getOpen_datetime());
                intent.putExtra("close_datetime",model.getClose_datetime());
                intent.putExtra("grand_total",String.valueOf(model.getGrand_total()));
                intent.putExtra("star",model.getUser());
                getContext().startActivity(intent);

            }
        });
        if (valueCheck.equals("Open")){
            dataBinding.btnSlider.setEngageText("Slide to close");
            dataBinding.btnSlider.setCompletedText("Closed");
            dataBinding.imgCall.setVisibility(View.VISIBLE);
        }else if (valueCheck.equals("Close")){
            dataBinding.btnSlider.setEngageText("View Bill");
            dataBinding.btnSlider.setCompletedText("Generated");
            dataBinding.imgCall.setVisibility(View.GONE);
        }else if (valueCheck.equals("Pending")){
            dataBinding.btnSlider.setEngageText("Slide to open");
            dataBinding.btnSlider.setCompletedText("Opened");
            dataBinding.imgCall.setVisibility(View.GONE);
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
//                    getUpdatelistner().closeRequest(String.valueOf(model.getId()), position);
                    Intent intent = new Intent(getContext(), GenerateBillActivity.class);
                    intent.putExtra("serviceId", String.valueOf(model.getId()));
                    getContext().startActivity(intent);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            dataBinding.btnSlider.reset();
                        }
                    }, 1000);

                }else if (valueCheck.equals("Close")){
//                    getUpdatelistner().closeRequest(String.valueOf(model.getId()), position);
                    Intent intent = new Intent(getContext(), ViewBillActivity.class);
                    intent.putExtra("serviceId", String.valueOf(model.getId()));
                    getContext().startActivity(intent);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            dataBinding.btnSlider.reset();
                        }
                    }, 1000);
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
