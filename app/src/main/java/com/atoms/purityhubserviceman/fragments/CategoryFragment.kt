package com.atoms.purityhubserviceman.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.CategoryAdapter
import com.atoms.purityhubserviceman.databinding.FragmentCategoryBinding
import com.atoms.purityhubserviceman.model.BrandCategory
import com.atoms.purityhubserviceman.model.BrandCategoryData
import com.google.gson.GsonBuilder


class CategoryFragment : Fragment() {

    lateinit var binding: FragmentCategoryBinding
    var responseMsg = ""
    var nameValue = ""
    var emailValue = ""
    var mobileValue = ""
    var addValue = ""
    var stateId = ""
    var cityId = ""
    var base64Image = ""
    var otpId = ""
    var selectedCatIdValue= ""
    var selectedCatId = ArrayList<String>()
    private var categoryArray = ArrayList<BrandCategoryData>()

    //    var callback: onCateoryListener? = null
//    interface onCateoryListener{
//        fun onCategorySendListener(categoryName: String, id: Int)
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.tool.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tool.toolbarText.text = "Choose Category"
        binding.categoryRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        nameValue = arguments?.getString("nameValue").toString()
        emailValue = arguments?.getString("emailValue").toString()
        mobileValue = arguments?.getString("mobileValue").toString()
        addValue = arguments?.getString("addValue").toString()
        stateId = arguments?.getString("stateId").toString()
        cityId = arguments?.getString("cityId").toString()
        base64Image = arguments?.getString("base64Image").toString()
        otpId = arguments?.getString("otpId").toString()
        startLoading("Getting category...")
        getBrandsDetails(apiType = "Category")

        binding.continueBtn.setOnClickListener {

            if (selectedCatIdValue.isEmpty()){
                Toast.makeText(requireContext(), "Please select any one of them in category", Toast.LENGTH_SHORT).show()
            }else{
                val bundle = bundleOf("nameValue" to nameValue,
                    "emailValue" to emailValue, "mobileValue" to mobileValue,
                    "addValue" to addValue, "stateId" to stateId,
                    "cityId" to cityId, "base64Image" to base64Image,"otpId" to otpId,
                "selectedCatId" to selectedCatIdValue.toString())
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_categoryFragment_to_brandFragment, bundle)
            }


        }

//        binding.filterText.text = categoryShortName
        return binding.root
    }


    private fun getBrandsDetails(apiType: String) {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(false)
        categoryArray.clear()
        blackBlind.requestUrl(ServerApi.CATEGORY_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object : VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val brandCategory = gson.fromJson(
                    response,
                    BrandCategory::class.java
                )
                responseMsg = brandCategory.message
                if (brandCategory.success && brandCategory.status == 1) {
//                    Toast.makeText(requireContext(), brandCategory.message, Toast.LENGTH_SHORT)
//                        .show()

                    for (i in 0 until brandCategory.data.size) {

                    }


                    categoryArray = brandCategory.data as ArrayList<BrandCategoryData>
//                    categoryArray.add(0, BrandCategoryData(-1, "", "All", -1))
                    val brandCategoryAdapter =
                        CategoryAdapter(
                            requireContext(), categoryArray, updateListener,
                            "categoryShortName"
                        )
                    binding.categoryRv.adapter = brandCategoryAdapter
                        stopLoading()

                } else {
                    stopLoading()
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

//    override fun onAttach(activity: Activity) {
//        super.onAttach(activity)
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        callback = try {
//            activity as onCateoryListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException(
//                activity.toString()
//                        + " must implement onCateoryListener"
//            )
//        }
//    }

    var updateListener: UpdateListener = object : UpdateListener {
        override fun selectCategoryName(categorySelectedValue: String?, id: Int) {
            super.selectCategoryName(categorySelectedValue, id)
            if (categorySelectedValue.equals("true")) {
                if (!selectedCatId.contains(id.toString())) {
                    selectedCatId.add(id.toString())
                }
            } else if (categorySelectedValue.equals("false")) {
                if (selectedCatId.contains(id.toString())) {
                    selectedCatId.remove(id.toString())
                }
            }
            selectedCatIdValue = TextUtils.join(", ", selectedCatId)
            println("arrayValue = ${selectedCatIdValue.toString()}")
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
}