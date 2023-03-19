package com.atoms.purityhubserviceman.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.ServiceRequestAdapter
import com.atoms.purityhubserviceman.databinding.FragmentPendingBinding
import com.atoms.purityhubserviceman.extra.BlindGridSpacing
import com.atoms.purityhubserviceman.extra.BlindRecyclerMargin
import com.atoms.purityhubserviceman.extra.Constants
import com.google.gson.GsonBuilder
import com.myapplication.model.HistoryRequest
import com.myapplication.model.ServiceRequestData
import org.json.JSONObject
import java.text.FieldPosition
import java.util.*

class PendingFragment : Fragment(), UpdateListener {

    lateinit var binding: FragmentPendingBinding
    lateinit var sharedpref: Sharedpref
    private var tokenValue = ""
    var responseMsg = ""
    lateinit var serviceAdapter:ServiceRequestAdapter
    private var serviceRequestArray = ArrayList<ServiceRequestData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending, container, false)
        sharedpref = Sharedpref.getInstance(requireContext())
        tokenValue = sharedpref.getString(Constants.token)
        binding.pendingRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pendingRv.addItemDecoration(BlindRecyclerMargin(16,1))
        (Objects.requireNonNull(binding.pendingRv.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations =
            false
        binding.pendingRv.addItemDecoration(BlindGridSpacing(10))

            callUserServiceRequestHistory()



        return binding.root
    }

    private fun callUserServiceRequestHistory() {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.addParams("status","Pending")
        blackBlind.requestUrl(ServerApi.VIEW_HISTORY_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val historyRequest = gson.fromJson(
                    response,
                    HistoryRequest::class.java
                )
                responseMsg = historyRequest.message
                if (historyRequest.success && historyRequest.status == 1){
                    stopLoading()
                    Toast.makeText(requireContext(), historyRequest.message, Toast.LENGTH_SHORT).show()
                    serviceRequestArray = historyRequest.data as ArrayList<ServiceRequestData> /* = java.util.ArrayList<com.myapplication.model.ServiceRequestData> */
                    serviceAdapter = ServiceRequestAdapter(requireContext(), serviceRequestArray,
                        updateListener, "Pending")
                    binding.pendingRv.adapter = serviceAdapter

                }else {
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
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

    val updateListener = object: UpdateListener{
        override fun openRequest(requestId: String?, position: Int) {
            super.openRequest(requestId, position)
            openServiceRequest(requestId, position)
        }


    }

    private fun openServiceRequest(requestId: String?, position: Int) {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.addParams("service_request_id",requestId)
        blackBlind.addParams("status","open")
        blackBlind.requestUrl(ServerApi.OPEN_SERVICE_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {

                val jsonObject = JSONObject(response.toString())
                val status = jsonObject.getInt("status")
                val success = jsonObject.getBoolean("success")
                if (success && status == 1){
                    serviceAdapter.removeItem(position, serviceRequestArray)
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
//                    serviceRequestArray = historyRequest.data as ArrayList<ServiceRequestData> /* = java.util.ArrayList<com.myapplication.model.ServiceRequestData> */
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



//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//
//        if (isVisibleToUser){
//            startLoading("Getting data...")
//            callUserServiceRequestHistory()
//        }
//    }
}