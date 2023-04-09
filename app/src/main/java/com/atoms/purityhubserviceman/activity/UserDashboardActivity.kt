package com.atoms.purityhubserviceman.activity

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.ViewPagerAdapter
import com.atoms.purityhubserviceman.databinding.ActivityUserDashboardBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.fragments.ClosedFragment
import com.atoms.purityhubserviceman.fragments.OpenFragment
import com.atoms.purityhubserviceman.fragments.PendingFragment
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
               requestPermissions()

            }
            R.id.nav_visit_website ->{
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://www.purityhub.co.in")
                    intent.setPackage("com.android.chrome")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // chrome app not install
                    Toast.makeText(this@UserDashboardActivity, "Please install chrome browser", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.nav_earning->{
                Toast.makeText(this@UserDashboardActivity, "Coming Soon", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_review->{
                Toast.makeText(this@UserDashboardActivity, "Coming Soon", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }


    private fun requestPermissions() {
        // below line is use to request permission in the current activity.
        // this method is use to handle error in runtime permissions
        Dexter.withContext(this@UserDashboardActivity)
            // below line is use to request the number of permissions which are required in our app.
            .withPermissions( Manifest.permission.CALL_PHONE)
            // after adding permissions we are calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                        callPhone()
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
//                Toast.makeText(this@UserDashboardActivity, "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            // below line is use to run the permissions on same thread and to check the permissions
            .onSameThread().check()
    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "6262100101"))
        startActivity(intent)
    }

    // below is the shoe setting dialog method
    // which is use to display a dialogue message.
    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = AlertDialog.Builder(this@UserDashboardActivity)

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions")

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use call feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel()
            // below is the intent from which we are redirecting our user.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
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
}