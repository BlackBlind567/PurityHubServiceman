package com.atoms.purityhubserviceman.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.CategoryAdapter
import com.atoms.purityhubserviceman.adapter.CategoryBottomAdapter
import com.atoms.purityhubserviceman.databinding.FragmentCategoryBinding
import com.atoms.purityhubserviceman.databinding.FragmentCategoryBottomsBinding
import com.atoms.purityhubserviceman.model.BrandCategory
import com.atoms.purityhubserviceman.model.BrandCategoryData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.GsonBuilder
import com.myapplication.*

class CategoryBottomFragment(var categoryShortName: String) : BottomSheetDialogFragment(),
    UpdateListener {

    lateinit var binding: FragmentCategoryBottomsBinding
    var responseMsg = ""
    private var categoryArray = ArrayList<BrandCategoryData>()
    var callback: onCateoryListener? = null
    interface onCateoryListener{
        fun onCategorySendListener(categoryName: String, id: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_bottoms, container, false)
        binding.categoryRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        getBrandsDetails(apiType = "Category")
        binding.filterText.text = categoryShortName
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
                        categoryArray.add(0,BrandCategoryData(-1, "", "All", -1))
                        val brandCategoryAdapter =
                            CategoryBottomAdapter(
                                requireContext(), categoryArray, updateListener,
                                categoryShortName
                            )
                    binding.categoryRv.adapter = brandCategoryAdapter


                } else {
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        callback = try {
            activity as onCateoryListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement onCateoryListener"
            )
        }
    }

    var updateListener: UpdateListener = object: UpdateListener{
        override fun selectCategoryName(categoryName: String?, id: Int) {
            super.selectCategoryName(categoryName, id)

            callback!!.onCategorySendListener(categoryName!!, id)
            dismiss()
        }
    }
}