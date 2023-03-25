package com.atoms.purityhubserviceman.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.activity.UserDashboardActivity
import com.atoms.purityhubserviceman.adapter.BrandAdapter
import com.atoms.purityhubserviceman.databinding.FragmentBrandBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.BrandCategory
import com.atoms.purityhubserviceman.model.BrandCategoryData
import com.atoms.purityhubserviceman.model.SignUp
import com.google.gson.GsonBuilder

class BrandFragment : Fragment() {

    lateinit var binding: FragmentBrandBinding
    private var brandArray = ArrayList<BrandCategoryData>()
    var responseMsg = ""
    var nameValue = ""
    var emailValue = ""
    var mobileValue = ""
    var addValue = ""
    var stateId = ""
    var cityId = ""
    var base64Image = ""
    var otpId = ""
    var selectedCatId = ""
    var selectedBrandId = ArrayList<String>()
    var selectedBrandIdValue = ""
    lateinit var sharedpref: Sharedpref
//    var callback: onBrandListener? = null
//    interface onBrandListener{
//        fun onBrandSendListener(brandName: String, id: Int)
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand, container, false)
    (activity as AppCompatActivity).setSupportActionBar(binding.tool.toolbar)
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    binding.tool.toolbarText.text = "Choose Brand"
    sharedpref = Sharedpref.getInstance(requireContext())
        binding.brandsRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        nameValue = arguments?.getString("nameValue").toString()
        emailValue = arguments?.getString("emailValue").toString()
        mobileValue = arguments?.getString("mobileValue").toString()
        addValue = arguments?.getString("addValue").toString()
        stateId = arguments?.getString("stateId").toString()
        cityId = arguments?.getString("cityId").toString()
        base64Image = arguments?.getString("base64Image").toString()
        selectedCatId = arguments?.getString("selectedCatId").toString()
    otpId = arguments?.getString("otpId").toString()
    startLoading("Getting brands...")
        getBrandsDetails(apiType = "Brands")

        binding.regBtn.setOnClickListener {

            if(selectedBrandIdValue.isEmpty()){
                Toast.makeText(requireContext(), "Please select any one of them in category", Toast.LENGTH_SHORT).show()
            }else {
                startLoading("Registering Serviceman...")
                sendDataForRegistration()
            }
            println("nameValue = $nameValue")
            println("emailValue = $emailValue")
            println("mobileValue = $mobileValue")
            println("addValue = $addValue")
            println("stateId = $stateId")
            println("cityId = $cityId")
            println("base64Image = $base64Image")
            println("selectedCatId = $selectedCatId")
            println("selectedBrandIdValue = $selectedBrandIdValue")

        }

        return binding.root
    }

    private fun sendDataForRegistration() {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(false)
        blackBlind.addParams("name",nameValue)
        blackBlind.addParams("email",emailValue)
        blackBlind.addParams("brands_id",selectedBrandIdValue)
        blackBlind.addParams("service_req_catid",selectedCatId)
        blackBlind.addParams("otp_id",otpId)
        blackBlind.addParams("mobile",mobileValue)
        blackBlind.addParams("city_id",cityId)
        blackBlind.addParams("state_id",stateId)
        blackBlind.addParams("address",addValue)
        blackBlind.addParams("image",base64Image)
        blackBlind.requestUrl(ServerApi.SIGNUP_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object : VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val signUp = gson.fromJson(
                    response,
                    SignUp::class.java
                )
                responseMsg = signUp.message
                if (signUp.success && signUp.status == 1) {
//                    Toast.makeText(requireContext(), brandCategory.message, Toast.LENGTH_SHORT)
//                        .show()

                    stopLoading()
                    sharedpref.putString(Constants.token, signUp.data.token)
                    sharedpref.putString(Constants.name, signUp.data.name)
                    sharedpref.putString(Constants.email, signUp.data.email)
                    sharedpref.putString(Constants.mobile, signUp.data.mobile)
                    sharedpref.putString(Constants.login, "true")
//                    Toast.makeText(requireContext(), signUp.message, Toast.LENGTH_SHORT).show()
//                    val bundle = bundleOf("token" to signUp.data.token)
//                    Navigation.findNavController(registrationFragment.root)
//                        .navigate(R.id.action_registationFragment_to_userFragment, bundle)
                    val intent = Intent(requireContext(), UserDashboardActivity::class.java)
                    intent.putExtra("token", signUp.data.token)
                    startActivity(intent)

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
//                    brandArray.add(0,BrandCategoryData(-1, "", "All", -1))
                    val brandCategoryAdapter =
                        BrandAdapter(
                            requireContext(), brandArray, updateListener,
                            "brandSortName"
                        )
                    binding.brandsRv.adapter = brandCategoryAdapter

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
//            activity as onBrandListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException(
//                activity.toString()
//                        + " must implement onBrandListener"
//            )
//        }
//    }

    var updateListener: UpdateListener = object: UpdateListener {
        override fun selectBrandName(brandName: String?, id: Int) {
            super.selectBrandName(brandName, id)

            if (brandName.equals("true")) {
                if (!selectedBrandId.contains(id.toString())) {
                    selectedBrandId.add(id.toString())
                }
            } else if (brandName.equals("false")) {
                if (selectedBrandId.contains(id.toString())) {
                    selectedBrandId.remove(id.toString())
                }
            }
            selectedBrandIdValue = TextUtils.join(", ", selectedBrandId)
            println("arrayValue = $selectedBrandIdValue")
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