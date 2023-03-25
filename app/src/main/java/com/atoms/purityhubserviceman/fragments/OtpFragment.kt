package com.atoms.purityhubserviceman.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.activity.UserDashboardActivity
import com.atoms.purityhubserviceman.databinding.FragmentOtpBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.SendOtp
import com.atoms.purityhubserviceman.model.VerifyOtp
import com.google.gson.GsonBuilder


import java.lang.String.*
import java.util.concurrent.TimeUnit


class OtpFragment : Fragment() {

    lateinit var otpFragment: FragmentOtpBinding
    var mobileNUmber = ""
    var responseMsg = ""
    var otpValue = ""
    lateinit var sharedpref: Sharedpref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        otpFragment =DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false)
        mobileNUmber = arguments?.getString("mobile").toString()
        sharedpref = Sharedpref.getInstance(requireContext())
        otpFragment.otpTv.text = "Enter OTP sent to your mobile number $mobileNUmber"
        otpFragment.validateOtpBtn.setOnClickListener {
            otpValue = otpFragment.otpView.otp

            println("otpview == "+ otpFragment.otpView.otp)
//            println("otpview == "+ otpFragment.otpView. )

            if (otpValue.length < 6){

                otpFragment.otpView.showError()
            }else {
                startLoading("Verifying OTP...")
                validateOtp()
            }
        }

        object : CountDownTimer(30000, 1000) {
            // adjust the milli seconds here
            @SuppressLint("SetTextI18n", "DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                otpFragment.otpResend.text = "Resend OTP in " + format(
                    "%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )
            }

            override fun onFinish() {
                otpFragment.otpResend.text = "Resend OTP?"
            }
        }.start()

        otpFragment.otpResend.setOnClickListener {
            val textvalue = otpFragment.otpResend.text.toString()
            if (textvalue.equals("Resend OTP?")){
                startLoading("OTP sending again...")
             sendOtp()
            }

        }

        return otpFragment.root
    }

    private fun validateOtp() {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.addParams("mobile",mobileNUmber)
        blackBlind.addParams("otp",otpValue)
        blackBlind.headersRequired(false)
        blackBlind.requestUrl(ServerApi.VERIFY_OTP_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val verifyOtp = gson.fromJson(
                    response,
                    VerifyOtp::class.java
                )
                responseMsg = verifyOtp.message
                if (verifyOtp.success && verifyOtp.status == 1){
                    if (verifyOtp.data.type == "Non Registerd") {
                        stopLoading()
                        Toast.makeText(requireContext(), verifyOtp.message, Toast.LENGTH_SHORT)
                            .show()
                        println("otpValue == " + verifyOtp.data.otp_id)
                        val bundle = bundleOf(
                            "otpId" to verifyOtp.data.otp_id.toString(),
                            "number" to mobileNUmber
                        )
                        Navigation.findNavController(otpFragment.root)
                            .navigate(R.id.action_otpFragment_to_registationFragment, bundle)
                    }else if (verifyOtp.data.type == "Registerd"){
                        sharedpref.putString(Constants.token, verifyOtp.data.token)
                        sharedpref.putString(Constants.type, verifyOtp.data.type)
                        sharedpref.putString(Constants.name, verifyOtp.data.name)
                        sharedpref.putString(Constants.email, verifyOtp.data.email)
                        sharedpref.putString(Constants.mobile, verifyOtp.data.mobile)
                        sharedpref.putString(Constants.online, verifyOtp.data.online.toString())
                        sharedpref.putString(Constants.login, "true")

//                        val bundle = bundleOf("token" to verifyOtp.data.token)
//                        Navigation.findNavController(otpFragment.root)
//                            .navigate(R.id.action_otpFragment_to_userFragment, bundle)

                        val intent = Intent(requireContext(), UserDashboardActivity::class.java)
                        intent.putExtra("token", verifyOtp.data.token)
                        startActivity(intent)
                    }
                }else {
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendOtp() {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.addParams("mobile",mobileNUmber)
        blackBlind.headersRequired(false)
        blackBlind.requestUrl(ServerApi.SEND_OTP_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback{
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val sendOtp = gson.fromJson(
                    response,
                    SendOtp::class.java
                )
                responseMsg = sendOtp.message
                if (sendOtp.success && sendOtp.status == 1){
                    stopLoading()
                    Toast.makeText(requireContext(), sendOtp.message, Toast.LENGTH_SHORT).show()

//                    val bundle = bundleOf("mobile" to numberValue)
//                    Navigation.findNavController(numberCheckFragment.root)
//                        .navigate(R.id.action_numberCheckFragment_to_otpFragment, bundle)
                }else {
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    public fun startLoading(msg: String) {
        otpFragment.loading.layoutPage.visibility = View.VISIBLE
        otpFragment.loading.customLoading.playAnimation()
        otpFragment.loading.customLoading.loop(true)
        otpFragment.loading.customDialogMessage.text = msg
    }

    public fun stopLoading() {
        otpFragment.loading.customLoading.pauseAnimation()
        otpFragment.loading.layoutPage.visibility = View.GONE
    }
}