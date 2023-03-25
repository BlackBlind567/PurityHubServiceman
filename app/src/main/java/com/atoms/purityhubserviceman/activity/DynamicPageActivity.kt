package com.atoms.purityhubserviceman.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.navigation.Navigation
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.databinding.ActivityDynamicPageBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.DynamicPage
import com.google.gson.GsonBuilder
import org.json.JSONObject


class DynamicPageActivity : AppCompatActivity() {
    var tokenValue = ""
    var responseMsg = ""
    lateinit var binding: ActivityDynamicPageBinding
    lateinit var sharedpref: Sharedpref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dynamic_page)
        sharedpref = Sharedpref.getInstance(this@DynamicPageActivity)
        setSupportActionBar(binding.tool.toolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        tokenValue = sharedpref.getString(Constants.token)
        val pageId = intent.getStringExtra("PageId").toString()
        getPageData(pageId)
    }

    private fun getPageData(pageId: String) {
        val blackBlind = BlackBlind(this@DynamicPageActivity)
        blackBlind.addParams("page_id",pageId)
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.requestUrl(ServerApi.DYNAMIC_PAGE_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val dynamicPage = gson.fromJson(
                    response,
                    DynamicPage::class.java
                )
                responseMsg = dynamicPage.message
                if (dynamicPage.success && dynamicPage.status == 1){
                    binding.tool.toolbarText.text = dynamicPage.data.title.toString()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.descriptionText.text =
                            Html.fromHtml(dynamicPage.data.description, Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        binding.descriptionText.text = Html.fromHtml(dynamicPage.data.description);
                    }
                }else {
                    Toast.makeText(this@DynamicPageActivity, responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(this@DynamicPageActivity, responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()

    }
}