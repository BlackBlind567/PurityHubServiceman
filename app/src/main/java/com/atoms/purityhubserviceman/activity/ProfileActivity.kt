package com.atoms.purityhubserviceman.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.Sharedpref
import com.atoms.purityhubserviceman.databinding.ActivityProfileBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.GenerateBill
import com.atoms.purityhubserviceman.model.OtpBrand
import com.atoms.purityhubserviceman.model.OtpServiceCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var sharedpref: Sharedpref
    var token = ""
    var name = ""
    var email = ""
    var mobile = ""
    var address = ""
    var stateId = ""
    var cityId = ""
    var stateName = ""
    var cityName= ""
    var selectedBrandArray = ArrayList<String>()
    var selectedBrandArrayValue = ""
    var selectedCategoryArray = ArrayList<String>()
    var selectedCategoryArrayValue = ""
    private var otpBrandArray = java.util.ArrayList<OtpBrand>()
    private var otpCategoryArray = java.util.ArrayList<OtpServiceCategory>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        sharedpref = Sharedpref.getInstance(this@ProfileActivity)
        token = sharedpref.getString(Constants.token)
        name = sharedpref.getString(Constants.name)
        email = sharedpref.getString(Constants.email)
        mobile = sharedpref.getString(Constants.mobile)
        address = sharedpref.getString(Constants.address)
        stateId = sharedpref.getString(Constants.stateId)
        cityId = sharedpref.getString(Constants.cityId)
        stateName = sharedpref.getString(Constants.stateName)
        cityName = sharedpref.getString(Constants.cityName)
       val otpBrand = sharedpref.getArrayList(Constants.brandArray)
       val otpCategory = sharedpref.getArrayList(Constants.categoryArray)
        val gson = Gson()
        val type: Type = object : TypeToken<java.util.ArrayList<OtpBrand>?>() {}.type
        otpBrandArray = gson.fromJson<List<OtpBrand>>(otpBrand, type) as java.util.ArrayList<OtpBrand>
        val type2: Type = object : TypeToken<java.util.ArrayList<OtpServiceCategory>?>() {}.type
        otpCategoryArray = gson.fromJson<List<OtpServiceCategory>>(otpCategory, type2) as java.util.ArrayList<OtpServiceCategory>
        println("array == $otpBrandArray")
        println("array1 == $otpCategoryArray")

        for (i in 0 until otpBrandArray.size) {
            selectedBrandArray.add(otpBrandArray[i].name.toString())
            selectedBrandArrayValue = TextUtils.join(", ", selectedBrandArray)
        }

        for (i in 0 until otpCategoryArray.size) {
            selectedCategoryArray.add(otpCategoryArray[i].name.toString())
            selectedCategoryArrayValue = TextUtils.join(", ", selectedCategoryArray)
        }

        binding.proName.text = name
        binding.proTextAlpha.text = name[0].toString().uppercase()
        binding.proMobEmail.text = "$mobile | $email"
        binding.proAdd.setText(address)
        binding.proStateCity.setText(cityName + ", " + stateName)
        binding.proCategory.text = selectedCategoryArrayValue
        binding.proBrands.text = selectedBrandArrayValue
        binding.editIcon.setOnClickListener {
            val intent = Intent(this@ProfileActivity, ChangeStateCityActivity::class.java)
            startActivity(intent)
        }

        binding.editWork.setOnClickListener {
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.putExtra("activityName", "CategoryFragment")
            startActivity(intent)
        }
    }
}