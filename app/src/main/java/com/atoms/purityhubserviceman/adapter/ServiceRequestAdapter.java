package com.atoms.purityhubserviceman.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.atoms.purityhubserviceman.R;
import com.atoms.purityhubserviceman.databinding.ServiceRequestLayoutBinding;
import com.myapplication.model.ServiceRequestData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServiceRequestAdapter extends BlindAdapter<ServiceRequestData, ServiceRequestLayoutBinding> {

    public ServiceRequestAdapter(Context context, ArrayList<ServiceRequestData> arrayList) {
        super(context, arrayList);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.service_request_layout;
    }

    @Override
    public void onBindData(ServiceRequestData model, int position, ServiceRequestLayoutBinding dataBinding) {
            dataBinding.srServiceman.setText(model.getServiceman());
            dataBinding.srBrandCategory.setText(model.getBrand() + ", " + model.getService_category());
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
//        bind(dataBinding , model);

        dataBinding.srViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                val bundle = bundleOf("mobile" to numberValue)
//                Navigation.findNavController(numberCheckFragment.root)
//                        .navigate(R.id.action_numberCheckFragment_to_otpFragment, bundle)
                Bundle bundle = new Bundle();
                bundle.putString("servicemanName",model.getServiceman());
                bundle.putString("brandName",model.getBrand());
                bundle.putString("categoryName",model.getService_category());
                bundle.putString("image", (String) model.getImage());
                bundle.putString("state",model.getState());
                bundle.putString("city",model.getCity());
                bundle.putString("address",model.getAddress());
                bundle.putString("priority",model.getPriority());
                bundle.putString("problemType",model.getProblem_type());
                bundle.putString("remark",model.getRemark());
                bundle.putString("createdAt",model.getCreated_at());
//                Navigation.findNavController(dataBinding.getRoot())
//                        .navigate(R.id.action_historyServiceRequestFragment_to_detailHistoryServiceFragment, bundle);
            }
        });
    }

//    private void bind(ServiceRequestLayoutBinding dataBinding, ServiceRequestData model) {
//        boolean expanded = model.getExpanded();
//        dataBinding.subItemView.setVisibility(expanded ? View.VISIBLE : View.GONE);
//
//        dataBinding.srRemark.setText(model.getRemark());
//        dataBinding.srPriority.setText(model.getPriority());
//        dataBinding.srProblemType.setText(model.getProblem_type());
//    }

    @Override
    protected void onBindDataHolder(ServiceRequestData serviceRequestData, int position, ServiceRequestLayoutBinding mDataBinding, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClick(ServiceRequestData model, int position) {

    }
}
