package com.atoms.purityhubserviceman.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.Sharedpref
import com.atoms.purityhubserviceman.databinding.ActivityLoginBinding
import com.atoms.purityhubserviceman.extra.Constants


class LoginActivity : AppCompatActivity() {
    var loginValue= ""
    var nameValue= ""
    var activityName = ""
    lateinit var sharedpref: Sharedpref
    lateinit var loginActivity: ActivityLoginBinding
    lateinit var  navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivity = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sharedpref = Sharedpref.getInstance(this@LoginActivity)
        loginValue = sharedpref.getString(Constants.login)
        nameValue = sharedpref.getString(Constants.name).toString()
        activityName = intent.getStringExtra("activityName").toString()
        println("fdsfsdf = $activityName")
//        getLoginToken()
        navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        if (loginValue != "" && nameValue != "" && activityName == "null"){
            val intent = Intent(this@LoginActivity, UserDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }else if (activityName != "" && activityName.equals("CategoryFragment")){
            val bundle = bundleOf( "nameValue" to "CategoryFragment")
            navController.navigate(R.id.categoryFragment, bundle)
        }else {
            navController.navigate(R.id.numberCheckFragment)
        }

    }


    override fun onNavigateUp(): Boolean {
        backButtonLogic()
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        backButtonLogic()
        super.onBackPressed()
    }

    private fun isFragmentInBackStack(destinationId: Int): Boolean{
        return try {
            navController.getBackStackEntry(destinationId)
            true
        }catch (e: Exception){
            false
        }

    }

    private fun backButtonLogic(){
        val id = navController.currentDestination!!.id
//        if (isFragmentInBackStack(R.id.brandFragment)) {
//            println("brandFragment")
//            navController.popBackStack(R.id.action_brandFragment_to_categoryFragment2, true)
//
//        }else
//            if (isFragmentInBackStack(R.id.registationFragment) && id == R.id.categoryFragment){
//            println("numberCheckFragment")
//             navController.popBackStack(R.id.registationFragment,true, false )
//
//        }else
        if (isFragmentInBackStack(R.id.otpFragment) && id == R.id.registationFragment){
            println("registationFragment")
            navController.popBackStack()

        }
        else  if (activityName.equals("CategoryFragment")){
            finish()
        }else {
            navController.navigateUp()
        }
    }

    fun onBackPressedCustomAction() {
        // Your custom action here
    }

    override fun onSupportNavigateUp(): Boolean {
        backButtonLogic()
        return super.onSupportNavigateUp()
    }
}