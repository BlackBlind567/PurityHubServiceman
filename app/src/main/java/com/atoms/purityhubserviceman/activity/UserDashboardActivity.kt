package com.atoms.purityhubserviceman.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.ViewPagerAdapter
import com.atoms.purityhubserviceman.databinding.ActivityUserDashboardBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.fragments.ClosedFragment
import com.atoms.purityhubserviceman.fragments.OpenFragment
import com.atoms.purityhubserviceman.fragments.PendingFragment
import com.atoms.purityhubserviceman.model.VerifyOtp
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import org.json.JSONObject


class UserDashboardActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {

    lateinit var binding: ActivityUserDashboardBinding
    var drawerLayout: DrawerLayout? = null
    var navSwitchStatus: SwitchCompat? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    var tokenValue = ""
    var responseMsg = ""
    var name = ""
    var updateListener: UpdateListener? = null
    var email = ""
    var apiCallCheck = false
    var online = ""
    lateinit var sharedpref: Sharedpref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard)
        setSupportActionBar(binding.tool.toolbar);
        binding.tool.toolbarText.text = "Purity Hub"
        sharedpref = Sharedpref.getInstance(this@UserDashboardActivity)
        name = sharedpref.getString(Constants.name)
        online = sharedpref.getString(Constants.online)
        println("nameValue == $name")
//        binding.tool.toolbarText.setTextColor(ContextCompat.getColor(this@UserDashboardActivity,
//            R.color.text_active_color
//        ))
        binding.tool.toolbarText.setPadding(40,0,0,0);
        binding.tool.toolbarText.textSize = 25f
        drawerLayout = findViewById(R.id.drawer_layout)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        tokenValue = sharedpref.getString(Constants.token)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this@UserDashboardActivity, drawerLayout, binding.tool.toolbar,
                R.string.nav_open,
                R.string.nav_close
            )

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!);
//        binding.drawerLayout.setDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState();
        binding.navView.setNavigationItemSelectedListener(this@UserDashboardActivity)
//         to make the Navigation drawer icon always appear on the action bar

        ViewCompat.setLayoutDirection(binding.tool.toolbar, ViewCompat.LAYOUT_DIRECTION_RTL)

        binding.tool.toolbar.setNavigationOnClickListener {
            if (drawerLayout!!.isDrawerOpen(Gravity.RIGHT)){
                drawerLayout!!.closeDrawer(Gravity.RIGHT)
            }else {
                drawerLayout!!.openDrawer(Gravity.RIGHT)
            }
        }
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val navTitle = headerView.findViewById<View>(R.id.nav_header_title) as TextView
        navSwitchStatus = headerView.findViewById<View>(R.id.switchcompat) as SwitchCompat
        navTitle.text = "Hi ${name},"

        navSwitchStatus!!.setOnCheckedChangeListener(this)

        if (online.equals("0")){
            navSwitchStatus!!.isChecked = false
        }else if (online.equals("1")){
            navSwitchStatus!!.isChecked = true
        }



        setupViewPager(binding.mainContent.viewpager);
        binding.mainContent.tabs.setupWithViewPager(binding.mainContent.viewpager);

    }

    private fun updateServiceManStatus(updateStatus: String) {
        val blackBlind = BlackBlind(this@UserDashboardActivity)
        blackBlind.addParams("online",updateStatus)
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.requestUrl(ServerApi.UPDATE_SERVICEMAN_STATUS_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
                val jsonObject = JSONObject(response.toString())
                val msg = jsonObject.getString("message")
                val status = jsonObject.getInt("status")
                val success = jsonObject.getBoolean("success")
                responseMsg = msg
                if (success && status == 1) {
                    Toast.makeText(this@UserDashboardActivity, responseMsg, Toast.LENGTH_SHORT).show()

                }else {
                    Toast.makeText(this@UserDashboardActivity, responseMsg, Toast.LENGTH_SHORT).show()
                    navSwitchStatus!!.setOnCheckedChangeListener (null);
                    if(updateStatus.equals("0")){

                        navSwitchStatus!!.isChecked = false
                        navSwitchStatus!!.setOnCheckedChangeListener(this@UserDashboardActivity)
                    }else if (updateStatus.equals("1")){
                        navSwitchStatus!!.isChecked = true
                        navSwitchStatus!!.setOnCheckedChangeListener(this@UserDashboardActivity)
                    }
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(this@UserDashboardActivity, responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(PendingFragment(), "Pending")
        adapter.addFragment(OpenFragment(), "Open")
        adapter.addFragment(ClosedFragment(), "Closed")
        viewPager.adapter = adapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_profile -> {
//                val intent = Intent(this,ProfileActivity::class.java)
//                startActivity(intent)
            }
            R.id.nav_call_us -> {
                val intent = Intent(this@UserDashboardActivity, DynamicPageActivity::class.java)
                intent.putExtra("PageId", "5")
                startActivity(intent)
            }
            R.id.nav_how_it_work -> {
                val intent = Intent(this@UserDashboardActivity, DynamicPageActivity::class.java)
                intent.putExtra("PageId", "4")
                startActivity(intent)
            }

            R.id.nav_about_us -> {
                val intent = Intent(this@UserDashboardActivity, DynamicPageActivity::class.java)
                intent.putExtra("PageId", "1")
                startActivity(intent)
            }

            R.id.nav_privacy_policy -> {
                val intent = Intent(this@UserDashboardActivity, DynamicPageActivity::class.java)
                intent.putExtra("PageId", "2")
                startActivity(intent)
            }

            R.id.nav_temrs -> {
                val intent = Intent(this@UserDashboardActivity, DynamicPageActivity::class.java)
                intent.putExtra("PageId", "3")
                startActivity(intent)
            }
            R.id.nav_logout -> {
                sharedpref.clearData()
                val intent = Intent(this@UserDashboardActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }


    public fun startLoading(msg: String) {
        binding.mainContent.loading.layoutPage.visibility = View.VISIBLE
        binding.mainContent.loading.customLoading.playAnimation()
        binding.mainContent.loading.customLoading.loop(true)
        binding.mainContent.loading.customDialogMessage.text = msg
    }

    public fun stopLoading() {
        binding.mainContent.loading.customLoading.pauseAnimation()
        binding.mainContent.loading.layoutPage.visibility = View.GONE
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        if(isChecked){
            navSwitchStatus!!.text = "Switch to Offline"
            updateServiceManStatus("0")
        }else {
            updateServiceManStatus("1")
            navSwitchStatus!!.text = "Switch to Online"
        }
    }

//    var updateListener = object: UpdateListener{
//
//    }
}