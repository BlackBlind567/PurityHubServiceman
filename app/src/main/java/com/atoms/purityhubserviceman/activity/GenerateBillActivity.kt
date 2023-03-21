package com.atoms.purityhubserviceman.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.databinding.ActivityGenerateBillBinding
import com.atoms.purityhubserviceman.model.GenerateBill

class GenerateBillActivity : AppCompatActivity() {
    private var generateBillArray = ArrayList<GenerateBill>()
    lateinit var binding: ActivityGenerateBillBinding
    var id = ""
    var title = ""
    var price = ""
    var quantity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_bill)

        if(intent != null){
            id = intent.getStringExtra("id").toString()
            title = intent.getStringExtra("title").toString()
            price = intent.getStringExtra("price").toString()
            quantity = intent.getStringExtra("quantity").toString()
            val itemData = GenerateBill(id,title, price, quantity)
            generateBillArray.add(itemData)
        }

        binding.addLayout.setOnClickListener{
            val intent = Intent(this@GenerateBillActivity, ProductsActivity::class.java)
            startActivity(intent)
        }



    }
}