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
import com.atoms.purityhubserviceman.databinding.FragmentOpenBinding
import com.atoms.purityhubserviceman.extra.BlindGridSpacing
import com.atoms.purityhubserviceman.extra.BlindRecyclerMargin
import com.atoms.purityhubserviceman.extra.Constants
import com.google.gson.GsonBuilder
import com.myapplication.model.HistoryRequest
import com.atoms.purityhubserviceman.model.ServiceRequestData
import java.util.*


class OpenFragment : Fragment(), UpdateListener, CloseRequestOtpFragment.OnServiceRequest {

    lateinit var binding: FragmentOpenBinding
    lateinit var sharedpref: Sharedpref
    private var tokenValue = ""
    var responseMsg = ""
    private var fragmentResume = false
    private var fragmentVisible = false
    private var fragmentOnCreated = false
    lateinit var serviceAdapter:ServiceRequestAdapter
    private var serviceRequestArray = ArrayList<ServiceRequestData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_open, container, false)
        sharedpref = Sharedpref.getInstance(requireContext())
        tokenValue = sharedpref.getString(Constants.token)
        binding.openRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.openRv.addItemDecoration(BlindRecyclerMargin(16,1))
        (Objects.requireNonNull(binding.openRv.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations =
            false
        binding.openRv.addItemDecoration(BlindGridSpacing(10))

//        if (isV)
        if(!fragmentResume && fragmentVisible) {

            startLoading("Getting Request...")
            callUserServiceRequestHistory()
        }
        return binding.root
    }

    private fun callUserServiceRequestHistory() {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.addParams("status","open")
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
                    serviceRequestArray = historyRequest.data as ArrayList<ServiceRequestData> /* = java.util.ArrayList<com.atoms.purityhubserviceman.model.ServiceRequestData> */
                     serviceAdapter = ServiceRequestAdapter(requireContext(), serviceRequestArray,
                        updateListener, "Open")
                    binding.openRv.adapter = serviceAdapter

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
        override fun closeRequest(requestId: String?, position: Int) {
            super.closeRequest(requestId, position)

            val bottomSheet = CloseRequestOtpFragment(requestId, position)
            bottomSheet.show(
                childFragmentManager,
                "CategoryFragment"
            )
        }

//        override fun initiateApiCallOpen(apiCall: String?) {
//            super.initiateApiCallOpen(apiCall)
//            println("call Api Open")
//            if (apiCall.equals("true")){
//                startLoading("Getting data...")
//                callUserServiceRequestHistory()
//            }
//        }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed){
            // only at fragment screen is resumed
            fragmentResume=true;
            fragmentVisible=false;
            fragmentOnCreated=true;
            startLoading("Getting data...")
            callUserServiceRequestHistory()
        }else  if (isVisibleToUser){        // only at fragment onCreated
            fragmentResume=false;
            fragmentVisible=true;
            fragmentOnCreated=true;
        }
        else if(!isVisibleToUser && fragmentOnCreated){// only when you go out of fragment screen
            fragmentVisible=false;
            fragmentResume=false;
        }
    }

    override fun OnServiceRequestListener(requestID: String, requestPosition: Int) {
        serviceAdapter.removeItem(requestPosition, serviceRequestArray)
    }
}