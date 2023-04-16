package com.atoms.purityhubserviceman.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.GenerateBillAdapter
import com.atoms.purityhubserviceman.databinding.ActivityGenerateBillBinding
import com.atoms.purityhubserviceman.extra.BlindRecyclerMargin
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.GenerateBill
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type

class GenerateBillActivity : AppCompatActivity(), UpdateListener {
    private var generateBillArray = java.util.ArrayList<GenerateBill>()
    lateinit var binding: ActivityGenerateBillBinding
    private var mEditor: SharedPreferences.Editor? = null
    var id = ""
    var title = ""
    var price = ""
    var mrp = ""
    var image = ""
    var quantity = ""
    var tokenValue = ""
    var totalItemPrice = ""
    var discountValue = ""
    var serviceId = ""
    var notChangedAmount = 0
    var billPaidValue = ""
    var remarkValue = ""
    var otpValue = ""
    var finalTotalItemPrice = 0
    var newItemArray = false
    var selectedQuantityId = ArrayList<String>()
    var selectedQuantityIdValue = ""
    var selectedProductId = ArrayList<String>()
    var selectedProductIdValue = ""
    lateinit var sharedpref: Sharedpref
    lateinit var prefs: SharedPreferences
    private lateinit var generateBillAdapter: GenerateBillAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_bill)
        sharedpref = Sharedpref.getInstance(this)
        tokenValue = sharedpref.getString(Constants.token)
        binding.itemLayout.layoutManager =
            LinearLayoutManager(this@GenerateBillActivity, LinearLayoutManager.VERTICAL, false)
        binding.tool.toolbarText.text = "Generate Bill"
        setSupportActionBar(binding.tool.toolbar)
        println("array1212121212 == $generateBillArray")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        prefs = PreferenceManager.getDefaultSharedPreferences(this@GenerateBillActivity)
        if (intent != null) {
            id = intent.getStringExtra("id").toString()
            title = intent.getStringExtra("title").toString()
            price = intent.getStringExtra("price").toString()
            mrp = intent.getStringExtra("mrp").toString()
            image = intent.getStringExtra("image").toString()
            quantity = intent.getStringExtra("quantity").toString()
            serviceId = intent.getStringExtra("serviceId").toString()
            totalItemPrice = intent.getIntExtra("totalItemPrice", 0).toString()
            newItemArray = intent.getBooleanExtra("itemArray", false)

            println("array1 == $selectedProductIdValue")
            println("array13 == $selectedQuantityIdValue")
            if (newItemArray) {
                binding.itemLayout.visibility = View.VISIBLE
                binding.productTv.visibility = View.VISIBLE
                println("array12 == ${generateBillArray.size}")
//                if (generateBillArray.size != 0){

                generateBillArray = getArrayList("arrayName")
//                }

                val itemData = GenerateBill(id, title, image, price, mrp, quantity, totalItemPrice)
                generateBillArray.add(itemData)
                generateBillAdapter = GenerateBillAdapter(
                    this@GenerateBillActivity,
                    generateBillArray, updateListener
                )
                binding.itemLayout.adapter = generateBillAdapter
                binding.itemLayout.addItemDecoration(BlindRecyclerMargin(32,1))
                for (i in 0 until generateBillArray.size) {
                    finalTotalItemPrice += generateBillArray[i].productTotalItemPrice.toInt()
                    selectedProductId.add(generateBillArray[i].productId)
                    selectedQuantityId.add(generateBillArray[i].productQuantity)
                    selectedProductIdValue = TextUtils.join(", ", selectedProductId)
                    selectedQuantityIdValue = TextUtils.join(", ", selectedQuantityId)
                }

                binding.btnLayout.visibility = View.VISIBLE
                binding.totalPrice.text = "\u20B9" + finalTotalItemPrice.toString()

                println("array == $generateBillArray")
            }else{
//                clearArrayList(generateBillArray)
            }

        }

        binding.addLayout.setOnClickListener {

            saveArrayList(generateBillArray, "arrayName")

            newItemArray = true
            val intent = Intent(this@GenerateBillActivity, ProductsActivity::class.java)
            intent.putExtra("arrayName", newItemArray)
            intent.putExtra("serviceId", serviceId)
            startActivity(intent)
        }

        binding.rbGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){
                R.id.bill_paid->{
                    binding.remarkTil.visibility = View.VISIBLE
                    billPaidValue = "Paid"
                }
                R.id.bill_not_paid->{
                    binding.remarkTil.visibility = View.GONE
                    billPaidValue = "Not Paid"
                    binding.remarkEt.text!!.clear()
                }
            }

        }

        binding.generateBill.setOnClickListener {

            discountValue = binding.discountEt.text.toString()
            remarkValue = binding.remarkEt.text.toString()
            otpValue = binding.otpView.otp
            if (otpValue.length < 4){
                binding.otpView.showError()
            }else if (billPaidValue == "") {
                Toast.makeText(this@GenerateBillActivity, "Please select bill is paid by user or not", Toast.LENGTH_SHORT).show()
            }else if (billPaidValue == "Paid" && remarkValue == ""){
                Toast.makeText(this@GenerateBillActivity, "Please fill remark", Toast.LENGTH_SHORT).show()
            }
            else {
                startLoading("sending data...")
                generateBillForUser()
            }


        }

        binding.applyTv.setOnClickListener {
           if(binding.applyTv.text == "Apply"){
               discountValue = binding.discountEt.text.toString()
               if (discountValue.equals("")){
                   Toast.makeText(this, "please enter discount amount", Toast.LENGTH_SHORT).show()
               }else{
                   binding.applyTv.text = "Applied"
                   notChangedAmount = finalTotalItemPrice
                   finalTotalItemPrice = finalTotalItemPrice.toInt() - discountValue.toInt()
                   binding.totalPrice.text = "\u20B9" + finalTotalItemPrice
               }
           }

        }

        binding.itemDiscount.setOnClickListener {
            binding.discountLayout.visibility = View.VISIBLE
            binding.viewBg.visibility = View.VISIBLE
        }

        binding.discountEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0){
                    binding.applyTv.text = "Apply"
                        finalTotalItemPrice = notChangedAmount
                    binding.totalPrice.text = "\u20B9" + finalTotalItemPrice
                }else {

                }
            }
        })

    }

    private fun generateBillForUser() {
        val blackBlind = BlackBlind(this@GenerateBillActivity)
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        if (discountValue.equals("")){
            discountValue = "0"
        }
        blackBlind.addParams("service_req_id", serviceId)
        blackBlind.addParams("product_id", selectedProductIdValue)
        blackBlind.addParams("quantity", selectedQuantityIdValue)
        blackBlind.addParams("discount", discountValue)
        blackBlind.addParams("otp", otpValue)
        blackBlind.addParams("remark", remarkValue)
        blackBlind.addParams("billStatus", billPaidValue)
        blackBlind.requestUrl(ServerApi.GENERATE_BILL_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object : VolleyCallback {
            override fun getResponse(response: String?) {

                val jsonObject = JSONObject(response.toString())
                val msg = jsonObject.getString("message")
                val status = jsonObject.getInt("status")
                val success = jsonObject.getBoolean("success")
                if (jsonObject.has("data")) {
                    val dataString = jsonObject.get("data")
                    if (dataString is JSONObject) {
                        stopLoading()
                        Toast.makeText(
                            this@GenerateBillActivity,
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (success && status == 1) {
                        stopLoading()
                        Toast.makeText(
                            this@GenerateBillActivity,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@GenerateBillActivity, UserDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        stopLoading()
                        Toast.makeText(
                            this@GenerateBillActivity,
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

//                    else if (dataString is JSONArray){
//                        val gsonBuilder = GsonBuilder()
//                        gsonBuilder.setDateFormat("M/d/yy hh:mm a")
//                        val gson = gsonBuilder.create()
//                        product = gson.fromJson(
//                            response,
//                            Product::class.java
//                        )
//                        responseMsg = product!!.message
//                        if (product!!.success && product!!.status == 1){
////                    Toast.makeText(this@ProductsActivity, responseMsg, Toast.LENGTH_SHORT).show()
//                            productArray = product!!.data as ArrayList<ProductData>
////                    for (i in 0 until product.data.size){
////
////                    }
//
//
//
//                        }else {
//
//                            Toast.makeText(this@ProductsActivity, responseMsg, Toast.LENGTH_SHORT).show()
//                        }


//                    }
                } else {
                    stopLoading()
                    Toast.makeText(
                        this@GenerateBillActivity,
                        jsonObject.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                val productsAdapter = ProductsAdapter(
//                    this@ProductsActivity,
//                    productArray
//                )
//                binding.productsRv.setAdapter(productsAdapter)

            }

            override fun getError(error: String?) {
                stopLoading()
//                Toast.makeText(this@GenerateBillActivity, responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getArrayList(key: String?): java.util.ArrayList<GenerateBill> {

        val gson = Gson()
//        val json: String = gson.toJson(generateBillArray)
        val json: String? = prefs.getString(key, null)
        val type: Type = object : TypeToken<java.util.ArrayList<GenerateBill>?>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveArrayList(list: java.util.ArrayList<GenerateBill>, key: String?) {

        mEditor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
//        sharedpref.putString(key, json)
        mEditor!!.putString(key, json)
        mEditor!!.apply()
    }

    private fun clearArrayList(list: java.util.ArrayList<GenerateBill>){
        mEditor = prefs.edit()
        mEditor!!.clear()
    }

    val updateListener = object : UpdateListener {
        override fun generatedRemoveBillData(
            productId: String?,
            productQuantity: String?,
            productPrice: String,
            position: Int
        ) {
            super.generatedRemoveBillData(productId, productQuantity, productPrice, position)
            if (!selectedProductId.contains(id)) {
                selectedProductId.remove(id)
            }
            if (!selectedQuantityId.contains(id)) {
                selectedQuantityId.remove(id)
            }

            println("array14 == $selectedProductId")
            println("array143 == $selectedQuantityId")
            println("array144 == $generateBillArray")
            generateBillArray.removeAt(position)
            generateBillAdapter.notifyItemRemoved(position)
            generateBillAdapter.notifyDataSetChanged()
            println("array146 == $generateBillArray")
            saveArrayList(generateBillArray, "arrayName")
            println("array145 == $generateBillArray")
            finalTotalItemPrice -= productPrice.toInt()
            binding.totalPrice.text = "\u20B9" + finalTotalItemPrice.toString()
            if(finalTotalItemPrice == 0){
                binding.itemLayout.visibility = View.GONE
                binding.productTv.visibility = View.GONE
                binding.btnLayout.visibility = View.GONE
                clearArrayList(generateBillArray)
            }
        }
    }

    public fun startLoading(msg: String) {
        binding.loading.layoutPage.visibility = View.VISIBLE
        binding.loading.customLoading.playAnimation()
        binding.loading.customLoading.loop(true)
        binding.loading.customDialogMessage.text = msg
    }

    public fun stopLoading() {
        binding.loading.customLoading.pauseAnimation()
        binding.loading.layoutPage.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}