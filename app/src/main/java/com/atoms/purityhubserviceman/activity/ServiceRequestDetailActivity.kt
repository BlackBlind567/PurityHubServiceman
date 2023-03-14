package com.atoms.purityhubserviceman.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.databinding.ActivityServiceRequestBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlin.jvm.internal.Intrinsics

class ServiceRequestDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityServiceRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_request)

        setSupportActionBar(binding.detailsToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.detailsToolbar.toolbarText.text = "Request Details"
        val servicemanName = intent.getStringExtra("servicemanName").toString()
        val brandName = intent.getStringExtra("brandName").toString()
        val categoryName = intent.getStringExtra("categoryName").toString()
        val image = intent.getStringExtra("image").toString()
        val state = intent.getStringExtra("state").toString()
        val city = intent.getStringExtra("city").toString()
        val address = intent.getStringExtra("address").toString()
        val priority = intent.getStringExtra("priority").toString()
        val problemType = intent.getStringExtra("problemType").toString()
        val remark = intent.getStringExtra("remark").toString()
        val createdAt = intent.getStringExtra("createdAt").toString()

        binding.srServiceman.text = servicemanName
        binding.srBrand.text = brandName
        binding.srCategory.text = categoryName
//        binding.srProblemImage.text = image
        binding.srState.text = state
        binding.srCity.text = city
        binding.srAddress.text = address
        binding.srProblemPriority.text = priority
        binding.srProblemType.text = problemType
        binding.srRemark.text = remark
        binding.srReqDate.text = createdAt


        binding.srProblemImage.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            val var21: LayoutInflater = this.layoutInflater
            Intrinsics.checkNotNullExpressionValue(var21, "layoutInflater")
            val convertView: View = var21.inflate(R.layout.image_dialog_layout, null)
            Intrinsics.checkNotNullExpressionValue(alertDialog, "alertDialog")
            val var22 = alertDialog.window
            Intrinsics.checkNotNull(var22)
//            var22!!.setBackgroundDrawable(ColorDrawable(0))
//            val convertView = var21.inflate(R.layout.image_dialog_layout, null)
            val iv = convertView.findViewById<ImageView>(R.id.request_image)
            Glide.with(this)
                .load(R.drawable.camera)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv)
//            val body = dialog.findViewById(R.id.body) as TextView
//            body.text = title
//            val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
//            val noBtn = dialog.findViewById(R.id.noBtn) as Button
//            yesBtn.setOnClickListener {
//                dialog.dismiss()
//            }
//            noBtn.setOnClickListener {
//                dialog.dismiss()
//            }
            alertDialog.show()
        }
    }
}