package com.atoms.purityhubserviceman

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.Sharedpref
import com.atoms.purityhubserviceman.UserDashboardActivity
import com.atoms.purityhubserviceman.databinding.ActivityLoginBinding
import com.atoms.purityhubserviceman.extra.Constants


class LoginActivity : AppCompatActivity() {
    var loginValue= ""
    var nameValue= ""
    lateinit var sharedpref: Sharedpref
    lateinit var loginActivity: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivity = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sharedpref = Sharedpref.getInstance(this@LoginActivity)
        loginValue = sharedpref.getString(Constants.login)
        nameValue = sharedpref.getString(Constants.name)
//        getLoginToken()

//        if (loginValue != "" && nameValue != ""){
//            val intent = Intent(this@LoginActivity, UserDashboardActivity::class.java)
//            startActivity(intent)
//            finish()
//        }else{
//            val navController: NavController =
//                Navigation.findNavController(this, R.id.nav_host_fragment)
//            navController.navigate(R.id.numberCheckFragment)
//        }

    }

//    private fun getLoginToken() {
//        val blackBlind = BlackBlind(this)
//        blackBlind.headersRequired(false)
//        blackBlind.requestUrl(ServerApi.LOGIN_REQUEST)
//        blackBlind.addParams("username","rohit@gmail.com")
//        blackBlind.addParams("password","12345678")
//        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback{
//            override fun getResponse(response: String?) {
//                val gsonBuilder = GsonBuilder()
//                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
//                val gson = gsonBuilder.create()
//                val login = gson.fromJson(
//                    response,
//                    Login::class.java
//                )
//
//                if (login.status == 1  && login.success){
//                    loginArray.add(login.data)
//
//                }
//            }
//
//            override fun getError(error: String?) {
//
//            }
//        })
//
//    }
}