package com.atoms.purityhubserviceman.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.Sharedpref
import com.atoms.purityhubserviceman.databinding.ActivityProfileBinding
import com.atoms.purityhubserviceman.extra.Constants


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

        binding.proName.text = name
        binding.proTextAlpha.text = name[0].toString().uppercase()
        binding.proMobEmail.text = "$mobile | $email"
        binding.proAdd.setText(address)
        binding.proStateCity.setText(cityName + ", " + stateName)

        binding.editIcon.setOnClickListener {
            val intent = Intent(this@ProfileActivity, ChangeStateCityActivity::class.java)
            startActivity(intent)
        }
    }
}