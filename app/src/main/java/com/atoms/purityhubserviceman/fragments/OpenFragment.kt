package com.atoms.purityhubserviceman.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
                    serviceRequestArray = historyRequest.data as ArrayList<ServiceRequestData> /* = java.util.ArrayList<com.atoms.purityhubserviceman.model.ServiceRequestData> */
                     serviceAdapter = ServiceRequestAdapter(requireContext(), serviceRequestArray,
                        updateListener, "Open")
                    binding.openRv.adapter = serviceAdapter

                }else {
                    stopLoading()
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                stopLoading()
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

        override fun makeCall(phoneNumber: String?) {
            super.makeCall(phoneNumber)
            requestPermissions(phoneNumber!!)
        }
    }

    private fun requestPermissions(phoneNumber:String) {
        // below line is use to request permission in the current activity.
        // this method is use to handle error in runtime permissions
        Dexter.withContext(requireContext())
            // below line is use to request the number of permissions which are required in our app.
            .withPermissions( Manifest.permission.CALL_PHONE)
            // after adding permissions we are calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                        callPhone(phoneNumber)
//                        Toast.makeText(requireContext(), "All the permissions are granted..", Toast.LENGTH_SHORT).show()
                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently, we will show user a dialog message.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    // this method is called when user grants some permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                // we are displaying a toast message for error message.
                Toast.makeText(requireContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            // below line is use to run the permissions on same thread and to check the permissions
            .onSameThread().check()
    }

    // below is the shoe setting dialog method
    // which is use to display a dialogue message.
    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = AlertDialog.Builder(requireContext())

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions")

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel()
            // below is the intent from which we are redirecting our user.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // this method is called when user click on negative button.
            dialog.cancel()
        }
        // below line is used to display our dialog
        builder.show()
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
    private fun callPhone(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber))
        startActivity(intent)
    }
}