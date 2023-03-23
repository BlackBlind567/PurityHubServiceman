package com.atoms.purityhubserviceman.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.GenerateBillAdapter
import com.atoms.purityhubserviceman.adapter.ServiceRequestAdapter
import com.atoms.purityhubserviceman.adapter.VIewBillAdapter
import com.atoms.purityhubserviceman.databinding.ActivityGenerateBillBinding
import com.atoms.purityhubserviceman.databinding.ActivityViewBillBinding
import com.atoms.purityhubserviceman.databinding.FragmentProductItemDetailBinding
import com.atoms.purityhubserviceman.extra.BlindRecyclerMargin
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.ProductData
import com.atoms.purityhubserviceman.model.ViewBill
import com.atoms.purityhubserviceman.model.ViewBillData
import com.google.gson.GsonBuilder
import com.myapplication.model.HistoryRequest
import com.myapplication.model.ServiceRequestData
import java.util.ArrayList

class ViewBillActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewBillBinding
    lateinit var sharedpref: Sharedpref
    private var tokenValue = ""
    var responseMsg = ""
    private var viewBillArray = ArrayList<ViewBillData>()
    var serviceId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_bill)
        binding.tool.toolbarText.text = "View Bill"
        setSupportActionBar(binding.tool.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedpref = Sharedpref.getInstance(this)
        tokenValue = sharedpref.getString(Constants.token)
        binding.viewBillRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewBillRv.addItemDecoration(BlindRecyclerMargin(16,1))
        serviceId = intent.getStringExtra("serviceId").toString()
        startLoading("Getting Bill...")
        viewBillRequest()

        binding.generateBill.setOnClickListener {
            startActivity(Intent(this@ViewBillActivity, UserDashboardActivity::class.java))
            finish()
        }
    }

    private fun viewBillRequest() {
        val blackBlind = BlackBlind(this@ViewBillActivity)
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.addParams("service_req_id",serviceId)
        blackBlind.requestUrl(ServerApi.GET_SERVICE_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val viewBill = gson.fromJson(
                    response,
                    ViewBill::class.java
                )
                responseMsg = viewBill.message
                if (viewBill.success && viewBill.status == 1){
                    stopLoading()
//                    Toast.makeText(requireContext(), "pending == " + historyRequest.message, Toast.LENGTH_SHORT).show()
                    viewBillArray = viewBill.data as ArrayList<ViewBillData> /* = java.util.ArrayList<com.myapplication.model.ServiceRequestData> */
                    val viewBillAdapter = VIewBillAdapter(
                        this@ViewBillActivity,
                        viewBillArray
                    )
                    binding.viewBillRv.adapter = viewBillAdapter
                    binding.totalPrice.text = "\u20B9" +viewBill.summery.grand_total
                }else {
                    stopLoading()
                    Toast.makeText(this@ViewBillActivity, responseMsg, Toast.LENGTH_SHORT).show()
                }
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

    override fun onSupportNavigateUp(): Boolean {

        startActivity(Intent(this@ViewBillActivity, UserDashboardActivity::class.java))
        finish()
        return super.onSupportNavigateUp()
    }
}