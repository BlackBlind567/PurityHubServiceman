package com.atoms.purityhubserviceman.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.StateCityAdapter
import com.atoms.purityhubserviceman.databinding.ActivityChangeStateCityBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.StateCity
import com.atoms.purityhubserviceman.model.StateCityData
import com.google.gson.GsonBuilder

import org.json.JSONObject
import java.util.ArrayList

class ChangeStateCityActivity : AppCompatActivity() {

    lateinit var binding: ActivityChangeStateCityBinding
    var stateId = ""
    var responseMsg = ""
    var cityId = ""
    var addValue = ""
    lateinit var sharedpref: Sharedpref
    private var stateArray = ArrayList<StateCityData>()
    private var cityArray = ArrayList<StateCityData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this, R.layout.activity_change_state_city)
        sharedpref = Sharedpref.getInstance(this@ChangeStateCityActivity)
        getCityStateDetails(apiType = "State")
        binding.tool.toolbarText.text = "Update Profile"
        setSupportActionBar(binding.tool.toolbar)
        binding.chooseState.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                stateId = stateArray[position].id.toString()
                getCityStateDetails(apiType = "City")
                binding.chooseState.setText(stateArray[position].name)
                val stateCityAdapter = StateCityAdapter(this@ChangeStateCityActivity, stateArray)
                binding.chooseState.setAdapter(stateCityAdapter)
                cityId = ""
                binding.chooseCity.text.clear()
            }

        binding.chooseCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                cityId = cityArray[position].id.toString()

                binding.chooseCity.setText(cityArray[position].name)
                val stateCityAdapter = StateCityAdapter(this@ChangeStateCityActivity, cityArray)
                binding.chooseCity.setAdapter(stateCityAdapter)
            }

        binding.updateBtn.setOnClickListener {
          validation()
        }
    }

    private fun validation() {
        binding.stateLayout.error = null
        binding.cityLayout.error = null
        binding.addressTil.error = null
        addValue = binding.addressEt.text.toString()

        var cancel = false
        var focusView: View? = null

        if (addValue == "") {
            focusView = binding.addressTil
            binding.addressTil.error = "Please enter address"
            cancel = true
        } else if (stateId == "") {
            focusView = binding.stateLayout
            binding.stateLayout.error = "Please choose any brand"
            cancel = true
        } else if (cityId == "") {
            focusView = binding.cityLayout
            binding.cityLayout.error = "Please choose any brand"
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()
        } else {
            startLoading("Please wait while updating profile...")
            updateProfile()

        }

    }

    private fun updateProfile() {
        val blackBlind = BlackBlind(this@ChangeStateCityActivity)
        blackBlind.headersRequired(true)
        blackBlind.authToken(sharedpref.getString(Constants.token))
        blackBlind.requestUrl(ServerApi.UPDATE_PROFILE_REQUEST)
        blackBlind.addParams("city_id",cityId)
        blackBlind.addParams("state_id",stateId)
        blackBlind.addParams("address",addValue)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
                val jsonObject = JSONObject(response.toString())
                val msg = jsonObject.getString("message")
                val status = jsonObject.getInt("status")
                val success = jsonObject.getBoolean("success")
                if (success && status == 1) {
                    stopLoading()
                    sharedpref.putString(Constants.address, addValue)
                    sharedpref.putString(Constants.stateId,stateId)
                    sharedpref.putString(Constants.cityId, cityId)
                    sharedpref.putString(Constants.stateName, binding.chooseState.text.toString())
                    sharedpref.putString(Constants.cityName,  binding.chooseCity.text.toString())
                    Toast.makeText(
                        this@ChangeStateCityActivity,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent =
                        Intent(this@ChangeStateCityActivity, UserDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    stopLoading()
                    Toast.makeText(
                            this@ChangeStateCityActivity,
                    msg,
                    Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun getError(error: String?) {
                stopLoading()
                Toast.makeText(
                    this@ChangeStateCityActivity,
                    error.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getCityStateDetails(apiType: String) {
        val blackBlind = BlackBlind(this@ChangeStateCityActivity)
        blackBlind.headersRequired(false)
        if (apiType.equals("State")) {
            stateArray.clear()
            blackBlind.requestUrl(ServerApi.GET_STATE_LIST)
        } else if (apiType.equals("City")) {
            cityArray.clear()
            blackBlind.requestUrl(ServerApi.GET_CITY_LIST)
            blackBlind.addParams("state_id", stateId)
        }
        blackBlind.executeRequest(Request.Method.POST, object : VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val stateCity = gson.fromJson(
                    response,
                    StateCity::class.java
                )
                responseMsg = stateCity.message
                if (stateCity.success && stateCity.status == 1) {
//                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
//
//                    for (i in 0 until stateCity.data.size) {
//
//                    }
                    if (apiType.equals("State")) {
                        stateArray = stateCity.data as ArrayList<StateCityData>
                        val stateCityAdapter = StateCityAdapter(this@ChangeStateCityActivity, stateArray)
                        binding.chooseState.setAdapter(stateCityAdapter)
                    } else if (apiType.equals("City")) {
                        cityArray = stateCity.data as ArrayList<StateCityData>
                        val stateCityAdapter = StateCityAdapter(this@ChangeStateCityActivity, cityArray)
                        binding.chooseCity.setAdapter(stateCityAdapter)
                    }


                } else {
                    Toast.makeText(this@ChangeStateCityActivity, responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(this@ChangeStateCityActivity, responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun startLoading(msg: String) {
        binding.loading.layoutPage.visibility = View.VISIBLE
        binding.loading.customLoading.playAnimation()
        binding.loading.customLoading.loop(true)
        binding.loading.customDialogMessage.text = msg
    }

    public fun stopLoading() {
        binding.loading.customLoading.pauseAnimation()
        binding.loading.layoutPage.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}