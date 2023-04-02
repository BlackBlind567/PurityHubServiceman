package com.atoms.purityhubserviceman.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.databinding.FragmentCloseRequestOtpBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject

class CloseRequestOtpFragment(var requestId: String?, var position: Int) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentCloseRequestOtpBinding
    var otpValue = ""
    var responseMsg = ""
    var tokenValue = ""
//    var callback: OnServiceRequest = parentFragment as OnServiceRequest

    var callback: OnServiceRequest? = null
    interface OnServiceRequest{
        fun OnServiceRequestListener(requestID: String, requestPosition: Int)
    }
    lateinit var sharedpref: Sharedpref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_close_request_otp, container, false)
        sharedpref = Sharedpref.getInstance(requireContext())
        tokenValue = sharedpref.getString(Constants.token)
        binding.validateOtpBtn.setOnClickListener {
            otpValue = binding.otpView.otp
            if (otpValue.length < 6){

                binding.otpView.showError()
            }else {
                startLoading("Verifying OTP...")
                sendDataForCloseRequest(otpValue)
            }


        }

        return binding.root
    }

    private fun sendDataForCloseRequest(otpValue: String) {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.addParams("service_request_id",requestId)
        blackBlind.addParams("status","closed")
        blackBlind.addParams("otp",otpValue)
        blackBlind.requestUrl(ServerApi.CLOSE_SERVICE_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {

                val jsonObject = JSONObject(response.toString())
                val status = jsonObject.getInt("status")
                val success = jsonObject.getBoolean("success")
                if (success && status == 1){
//                    serviceAdapter.removeItem(position, serviceRequestArray)
                    callback!!.OnServiceRequestListener(requestId!!, position)
                    dismiss()

                }
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
//                    Toast.makeText(requireContext(), historyRequest.message, Toast.LENGTH_SHORT).show()
//                    serviceRequestArray = historyRequest.data as ArrayList<ServiceRequestData> /* = java.util.ArrayList<com.atoms.purityhubserviceman.model.ServiceRequestData> */
//                    val serviceAdapter = ServiceRequestAdapter(requireContext(), serviceRequestArray,
//                        updateListener, "Pending")
//                    binding.pendingRv.adapter = serviceAdapter
//
//                }else {
//                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
//                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    public fun startLoading(msg: String) {
        binding.loading.layoutPage.visibility = View.VISIBLE
        binding.loading.customLoading.playAnimation()
        binding.loading.customLoading.loop(true)
        binding.loading.customDialogMessage.text = msg
    }

    public fun stopLoading() {
        binding.loading.customLoading.pauseAnimation()
        binding.loading.layoutPage.visibility = View.GONE
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        callback = try {
            parentFragment as OnServiceRequest
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement OnServiceRequest"
            )
        }
    }

}