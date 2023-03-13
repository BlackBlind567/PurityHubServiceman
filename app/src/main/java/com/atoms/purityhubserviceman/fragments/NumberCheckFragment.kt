package com.atoms.purityhubserviceman.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.android.volley.Request
import com.atoms.purityhubserviceman.BlackBlind
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.ServerApi
import com.atoms.purityhubserviceman.VolleyCallback
import com.atoms.purityhubserviceman.databinding.FragmentNumberCheckBinding
import com.atoms.purityhubserviceman.model.SendOtp
import com.google.gson.GsonBuilder

import java.util.regex.Matcher
import java.util.regex.Pattern


class NumberCheckFragment : Fragment() {

    lateinit var numberCheckFragment: FragmentNumberCheckBinding
    var numberValue = ""
    var responseMsg = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        numberCheckFragment = DataBindingUtil.inflate(inflater, R.layout.fragment_number_check, container, false)

        numberCheckFragment.sendOtpBtn.setOnClickListener {
            validation()
        }

        return numberCheckFragment.root
    }

    private fun validation() {
        numberValue = numberCheckFragment.numberEt.text.toString()
        numberCheckFragment.numberTil.error = null

        var cancel = false
        var focusView: View? = null

        if (numberValue.isBlank() || numberValue.length < 10){
            cancel = true
            focusView = numberCheckFragment.numberTil
            numberCheckFragment.numberTil.error = "Please enter number"
        }else if (!isMobileValidation(numberValue)){
            cancel = true
            focusView = numberCheckFragment.numberTil
            numberCheckFragment.numberTil.error = "Please enter correct number"
        }

        if (cancel){
            focusView!!.requestFocus()
        }else{
            startLoading("Please wait ...")
            sendOtp()
        }
    }

    private fun sendOtp() {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.addParams("mobile",numberValue)
        blackBlind.addParams("type","2")
        blackBlind.headersRequired(false)
        blackBlind.requestUrl(ServerApi.SEND_OTP_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
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

                    val bundle = bundleOf("mobile" to numberValue)
                    Navigation.findNavController(numberCheckFragment.root)
                        .navigate(R.id.action_numberCheckFragment_to_otpFragment, bundle)
                }else {
                    stopLoading()
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isMobileValidation(number: String): Boolean {
        val regex = "^[6-9]\\d{9}\$"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(number)
        return matcher.matches()
    }

    public fun startLoading(msg: String) {
        numberCheckFragment.loading.layoutPage.visibility = View.VISIBLE
        numberCheckFragment.loading.customLoading.playAnimation()
        numberCheckFragment.loading.customLoading.loop(true)
        numberCheckFragment.loading.customDialogMessage.text = msg
    }

    public fun stopLoading() {
        numberCheckFragment.loading.customLoading.pauseAnimation()
        numberCheckFragment.loading.layoutPage.visibility = View.GONE
    }
}