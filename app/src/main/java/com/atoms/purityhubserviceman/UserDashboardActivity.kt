package com.atoms.purityhubserviceman

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.atoms.purityhubserviceman.adapter.ViewPagerAdapter
import com.atoms.purityhubserviceman.databinding.ActivityUserDashboardBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.fragments.ClosedFragment
import com.atoms.purityhubserviceman.fragments.OpenFragment
import com.atoms.purityhubserviceman.fragments.PendingFragment
import com.google.android.material.navigation.NavigationView


class UserDashboardActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener{

    lateinit var binding: ActivityUserDashboardBinding
    var drawerLayout: DrawerLayout? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    var tokenValue = ""
    var responseMsg = ""
    var name = ""
    var email = ""
    lateinit var sharedpref: Sharedpref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard)
        setSupportActionBar(binding.tool.toolbar);
        binding.tool.toolbarText.text = "Purity Hub"
        sharedpref = Sharedpref.getInstance(this@UserDashboardActivity)
        name = sharedpref.getString(Constants.name)
        println("nameValue == $name")
//        binding.tool.toolbarText.setTextColor(ContextCompat.getColor(this@UserDashboardActivity,
//            R.color.text_active_color
//        ))
        binding.tool.toolbarText.setPadding(40,0,0,0);
        binding.tool.toolbarText.textSize = 25f
        drawerLayout = findViewById(R.id.drawer_layout)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        sharedpref = Sharedpref.getInstance(this)
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
        navTitle.text = "Hi ${name},"


        setupViewPager(binding.mainContent.viewpager);
        binding.mainContent.tabs.setupWithViewPager(binding.mainContent.viewpager);
        val tabText: String =
            binding.mainContent.tabs.getTabAt(binding.mainContent.tabs.selectedTabPosition)!!.text.toString()
        println("tabName == $tabText")
//        binding.mainContent.viewpager.cu
        binding.mainContent.viewpager
            .addOnPageChangeListener(object : OnPageChangeListener {
            // This method will be invoked when a new page becomes selected.
            override fun onPageSelected(position: Int) {
                Toast.makeText(
                    this@UserDashboardActivity,
                    "Selected page position: $position", Toast.LENGTH_SHORT
                ).show()
            }

            // This method will be invoked when the current page is scrolled
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            override fun onPageScrollStateChanged(state: Int) {
                // Code goes here
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

            }
            R.id.nav_how_it_work -> {

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

}