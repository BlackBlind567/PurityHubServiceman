package com.atoms.purityhubserviceman.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.ServiceRequestAdapter
import com.atoms.purityhubserviceman.databinding.ActivityGenerateBillBinding
import com.atoms.purityhubserviceman.databinding.ActivityViewBillBinding
import com.atoms.purityhubserviceman.databinding.FragmentProductItemDetailBinding
import com.atoms.purityhubserviceman.extra.BlindRecyclerMargin
import com.atoms.purityhubserviceman.extra.Constants
import com.google.gson.GsonBuilder
import com.myapplication.model.HistoryRequest
import com.myapplication.model.ServiceRequestData
import java.util.ArrayList

class ViewBillActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewBillBinding
    lateinit var sharedpref: Sharedpref
    private var tokenValue = ""
    var responseMsg = ""
    var serviceId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_bill)
        sharedpref = Sharedpref.getInstance(this)
        tokenValue = sharedpref.getString(Constants.token)
        binding.viewBillRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewBillRv.addItemDecoration(BlindRecyclerMargin(16,1))
        serviceId = intent.getStringExtra("serviceId").toString()
        startLoading("Getting Bill...")
        viewBillRequest()
    }

    private fun viewBillRequest() {
        val blackBlind = BlackBlind(this@ViewBillActivity)
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.addParams("service_req_id",serviceId)
        blackBlind.requestUrl(ServerApi.GET_SERVICE_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
//                val gsonBuilder = GsonBuilder()
//                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
//                val gson = gsonBuilder.create()
//                val historyRequest = gson.fromJson(
//                    response,
//                    HistoryRequest::class.java
//                )
//                responseMsg = historyRequest.message
//                if (historyRequest.success && historyRequest.status == 1){
//                    stopLoading()
//                    Toast.makeText(requireContext(), "pending == " + historyRequest.message, Toast.LENGTH_SHORT).show()
//                    serviceRequestArray = historyRequest.data as ArrayList<ServiceRequestData> /* = java.util.ArrayList<com.myapplication.model.ServiceRequestData> */
//                    serviceAdapter = ServiceRequestAdapter(requireContext(), serviceRequestArray,
//                        updateListener, "Pending")
//                    binding.pendingRv.adapter = serviceAdapter
//
//                }else {
//                    stopLoading()
//                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
//                }
            }

            override fun getError(error: String?) {
//                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun startLoading(msg: String) {
        binding.loading.layoutPage.visibility = View.VISIBLE
        binding.loading.customLoading.playAnimation()
        binding.loading.customLoading.loop(true)
        binding.loading.customDialogMessage.text = msg
    }

    fun stopLoading() {
        binding.loading.customLoading.pauseAnimation()
        binding.loading.layoutPage.visibility = View.GONE
    }
}