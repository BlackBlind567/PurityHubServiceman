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
import com.atoms.purityhubserviceman.adapter.BrandAdapter
import com.atoms.purityhubserviceman.adapter.BrandBottomAdapter
import com.atoms.purityhubserviceman.databinding.FragmentBrandsBottomBinding
import com.atoms.purityhubserviceman.model.BrandCategory
import com.atoms.purityhubserviceman.model.BrandCategoryData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.GsonBuilder



class BrandBottomFragment(var brandSortName: String) : BottomSheetDialogFragment(), UpdateListener {

    lateinit var binding: FragmentBrandsBottomBinding
    private var brandArray = ArrayList<BrandCategoryData>()
    var responseMsg = ""
    var callback: onBrandListener? = null
    interface onBrandListener{
        fun onBrandSendListener(brandName: String, id: Int)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_brands_bottom, container, false)
        binding.BrandsRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        getBrandsDetails(apiType = "Brands")
        binding.filterText.text = brandSortName

         return binding.root
    }

    private fun getBrandsDetails(apiType: String) {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(false)
        brandArray.clear()
        blackBlind.requestUrl(ServerApi.BRAND_REQUEST)
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


                    brandArray = brandCategory.data as ArrayList<BrandCategoryData>
                    brandArray.add(0,BrandCategoryData(-1, "", "All", -1))
                    val brandCategoryAdapter =
                        BrandBottomAdapter(
                            requireContext(), brandArray, updateListener,
                            brandSortName
                        )
                    binding.BrandsRv.adapter = brandCategoryAdapter


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
            activity as onBrandListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement onBrandListener"
            )
        }
    }

    var updateListener: UpdateListener = object: UpdateListener {
        override fun selectBrandName(brandName: String?, id: Int) {
            super.selectBrandName(brandName, id)

            callback!!.onBrandSendListener(brandName!!, id)
            dismiss()
        }
    }
}