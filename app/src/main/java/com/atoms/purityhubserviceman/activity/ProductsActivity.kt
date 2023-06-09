package com.atoms.purityhubserviceman.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.ProductsAdapter
import com.atoms.purityhubserviceman.databinding.ActivityProductsBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.fragments.*
import com.atoms.purityhubserviceman.model.Product
import com.atoms.purityhubserviceman.model.ProductData
import com.atoms.purityhubserviceman.model.ProductDataData
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

class ProductsActivity : AppCompatActivity(),
    CategoryBottomFragment.onCateoryListener, BrandBottomFragment.onBrandListener, UpdateListener,
    ProductItemDetailFragment.OnAddItemListener{
    private var productArray = ArrayList<ProductDataData>()

    lateinit var binding: ActivityProductsBinding
    var responseMsg = ""
    lateinit var sharedpref: Sharedpref
    private var tokenValue = ""
    private var categoryShortName = ""
    private var brandSortName = ""
    private var categoryId = ""
    private var brandId = ""
    private var serviceId = ""
    private var itemArray = false
    var product: Product? = null
    var pageNumber = 1
    private var isLoading = false
    private var isLastPage = false
    var lastPage = 0
    var currentPage = 0
    var perPage = 0
    private var layoutManager: LinearLayoutManager? = null
    private var productsAdapter: ProductsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products)
        binding.productToolbar.toolbarText.text = "Products"
        sharedpref = Sharedpref.getInstance(this)
        tokenValue = sharedpref.getString(Constants.token)
        itemArray = intent.getBooleanExtra("arrayName", false)
        serviceId = intent.getStringExtra("serviceId").toString()
        println("itemArray == $itemArray")


        layoutManager = LinearLayoutManager(this@ProductsActivity, LinearLayoutManager.VERTICAL, false)
        binding.productsRv.layoutManager = layoutManager
        getBrandsDetails(brandId,categoryId, pageNumber)

        binding.sortByCategory.setOnClickListener {
            val bottomSheet = CategoryBottomFragment(categoryShortName)
            bottomSheet.show(
                supportFragmentManager,
                "CategoryFragment"
            )
        }

        binding.quotaFilter.setOnClickListener {
            val bottomSheet = BrandBottomFragment(brandSortName)
            bottomSheet.show(
                supportFragmentManager,
                "BrandsFragment"
            )
        }

        productsAdapter = ProductsAdapter(
            this@ProductsActivity,
            productArray
        )
        binding.productsRv.setAdapter(productsAdapter)

        binding.productsRv.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                if (!isLastPage && !isLoading) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= perPage) {


                        getBrandsDetails(brandId,categoryId, pageNumber)

                    }
                }
            }
        })
    }

    private fun getBrandsDetails(brandId:String, categoryId: String, page:Int) {
        startLoading(msg = "Getting data...")
        val blackBlind = BlackBlind(this@ProductsActivity)
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        if (brandId != ""){
           blackBlind.addParams("brand_id", brandId)
        }
        if (categoryId != ""){
            blackBlind.addParams("cat_id", categoryId)
        }

        blackBlind.requestUrl(ServerApi.PRODUCT_REQUEST + page)
        blackBlind.executeRequest(Request.Method.POST, object: VolleyCallback {
            override fun getResponse(response: String?) {
                    println("ListData == $response")
                val jsonObject = JSONObject(response.toString())
               if (jsonObject.has("data")){
                   val dataString = jsonObject.getJSONObject("data")
                   val data = dataString.get("data")
                   if (data is JSONObject ){
                       stopLoading()
                       Toast.makeText(this@ProductsActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                   }else if (data is JSONArray){
                       val gsonBuilder = GsonBuilder()
                       gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                       val gson = gsonBuilder.create()
                       product = gson.fromJson(
                           response,
                           Product::class.java
                       )
                       responseMsg = product!!.message
                       if (product!!.success && product!!.status == 1){
                           currentPage = product!!.data.current_page
                           lastPage =  product!!.data.last_page
                           perPage =  product!!.data.per_page
//                    Toast.makeText(this@ProductsActivity, responseMsg, Toast.LENGTH_SHORT).show()
                           println("ListData12 == $currentPage")
                           println("ListData13 == $lastPage")
                         if (currentPage <= lastPage){
                             println("ListData11 == $response")
                             productsAdapter!!.addAll(product!!.data.data)
//                             productsAdapter!!.notifyDataSetChanged()
                             pageNumber += 1
                             stopLoading()
                             isLoading = false
                         }else{
                             stopLoading()
                            isLoading = true
                             isLastPage = true
                         }



                       }else {
                           isLoading = true
                           stopLoading()
                           Toast.makeText(this@ProductsActivity, responseMsg, Toast.LENGTH_SHORT).show()
                       }


                   }
               }else{
                   isLoading = true
                   stopLoading()
                   Toast.makeText(this@ProductsActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
               }


            }

            override fun getError(error: String?) {
                isLoading = true
                Toast.makeText(this@ProductsActivity, responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCategorySendListener(categoryName: String, id: Int) {
       categoryShortName = categoryName
        if (categoryShortName.equals("All")){
            binding.CategoryBy.visibility = View.GONE
            binding.CategoryBy.text = categoryShortName
            categoryId = ""
        }else{
            binding.CategoryBy.visibility = View.VISIBLE
            binding.CategoryBy.text = categoryShortName
            categoryId = id.toString()
        }

        productArray.clear()
        pageNumber = 1
        getBrandsDetails(brandId, categoryId, pageNumber)
    }

    override fun onBrandSendListener(brandName: String, id: Int) {
        brandSortName = brandName
        if (brandSortName.equals("All")){
            binding.brandsTv.visibility = View.GONE
            binding.brandsTv.text = brandSortName
            brandId =""
        }else{
            binding.brandsTv.visibility = View.VISIBLE
            binding.brandsTv.text = brandSortName
            brandId = id.toString()
        }

        productArray.clear()
        pageNumber = 1
        getBrandsDetails(brandId, categoryId, pageNumber)
    }

    override fun onAddItem(
        itemId: String,
        itemName: String,
        itemPrice: String,
        itemQuantity: String,
        totalItemPrice: Int,
        itemMrp: String,
        itemImage: String
    ) {

        val intent = Intent(this@ProductsActivity, GenerateBillActivity::class.java)
        intent.putExtra("id", itemId)
        intent.putExtra("title",itemName)
        intent.putExtra("price", itemPrice)
        intent.putExtra("quantity", itemQuantity)
        intent.putExtra("itemArray", itemArray)
        intent.putExtra("serviceId", serviceId)
        intent.putExtra("totalItemPrice", totalItemPrice.toInt())
        intent.putExtra("mrp", itemMrp)
        intent.putExtra("image", itemImage)
        startActivity(intent)
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


}