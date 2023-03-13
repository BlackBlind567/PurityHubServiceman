package com.atoms.purityhubserviceman.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Base64OutputStream
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.BrandCategoryAdapter
import com.atoms.purityhubserviceman.adapter.StateCityAdapter
import com.atoms.purityhubserviceman.databinding.FragmentRegistationBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.model.*
import com.google.gson.GsonBuilder
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import id.zelory.compressor.Compressor
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

import java.util.regex.Matcher
import java.util.regex.Pattern


class RegistrationFragment : Fragment() {

    lateinit var binding: FragmentRegistationBinding
    var responseMsg = ""
    var stateId = ""
    var cityId = ""
    var categoryId = ""
    var problemType = ""
    var otpId = ""
    var mobileNumber = ""
    var priorityType = ""
    private var tokenValue = ""
    var brandId = ""
    var base64Image = ""
    var remarkValue = ""
    var addValue = ""
    var longitude = ""
    var latitude = ""
    val problemArray = arrayOf<String>("Ajay", "Prakesh", "Michel", "John", "Sumit")
    val priorityArray = arrayOf<String>("High", "Intermediate", "Low", "John", "Sumit")
    private var photoPaths = java.util.ArrayList<String>()
    private var brandArray = ArrayList<BrandCategoryData>()
    private var categoryArray = ArrayList<BrandCategoryData>()
    private var stateArray = ArrayList<StateCityData>()
    private var cityArray = ArrayList<StateCityData>()
    private val CAMERA_REQUEST = 1888
    private val PERMISSION_REQUEST_CODE = 200
    lateinit var sharedpref: Sharedpref
//    var serviceman: Serviceman? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registation, container, false
        )
        otpId = arguments?.getString("otpId").toString()
        mobileNumber = arguments?.getString("number").toString()
        sharedpref = Sharedpref.getInstance(requireContext())
        tokenValue = sharedpref.getString(Constants.token)
        checkPermission()
//        binding.submitRequestBtn.setOnClickListener {
//            validation()
//
//        }
        binding.chooseProblemAt.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item, problemArray
            )
        )
        binding.choosePriorityAt.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item, priorityArray
            )
        )

        binding.chooseProblemAt.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                problemType = problemArray[position].toString()
                ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, problemArray
                )
            }

        binding.choosePriorityAt.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                priorityType = priorityArray[position].toString()
                ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, priorityArray
                )
            }

        binding.chooseBrand.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                brandId = brandArray[position].id.toString()
                binding.chooseBrand.setText(brandArray[position].name)

                val brandCategoryAdapter = BrandCategoryAdapter(requireContext(), brandArray)
                binding.chooseBrand.setAdapter(brandCategoryAdapter)
            }

        binding.ivAddLayout.setOnClickListener {
            uploadFile()
        }

        binding.chooseCategory.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
//            binding.chooseCategory.setText(brandCategoryArray[position].name)
                categoryId = categoryArray[position].id.toString()
                binding.chooseCategory.setText(categoryArray[position].name)
                getBrandsDetails(apiType = "Category")
                val brandCategoryAdapter = BrandCategoryAdapter(requireContext(), categoryArray)
                binding.chooseCategory.setAdapter(brandCategoryAdapter)
//            val brandCategoryAdapter = BrandCategoryAdapter(requireContext(),brandCategoryArray )
//            binding.chooseCategory.setAdapter(brandCategoryAdapter)
            }

        binding.chooseState.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                stateId = stateArray[position].id.toString()
                getCityStateDetails(apiType = "City")
                binding.chooseState.setText(stateArray[position].name)
                val stateCityAdapter = StateCityAdapter(requireContext(), stateArray)
                binding.chooseState.setAdapter(stateCityAdapter)
            }

        binding.chooseCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                cityId = cityArray[position].id.toString()

                binding.chooseCity.setText(cityArray[position].name)
                val stateCityAdapter = StateCityAdapter(requireContext(), cityArray)
                binding.chooseState.setAdapter(stateCityAdapter)
            }

        getBrandsDetails(apiType = "Brands")
        getBrandsDetails(apiType = "Category")
        getCityStateDetails(apiType = "State")




        return binding.root
    }

    private fun validation() {
        binding.brandLayout.error = null
        binding.categoryLayout.error = null
        binding.problemLayout.error = null
        binding.stateLayout.error = null
        binding.cityLayout.error = null
        binding.priorityLayout.error = null
        binding.remarkTil.error = null
        binding.addressTil.error = null
//        binding.addImgLayout.error = null

//        brandId = binding.chooseBrand.text.toString()
//        categoryId = binding.chooseCategory.text.toString()
//        problemType = binding.chooseProblemAt.text.toString()
        addValue = binding.addressEt.text.toString()
//        stateId = binding.chooseState.text.toString()
//        cityId = binding.chooseCity.text.toString()
//        priorityType = binding.choosePriorityAt.text.toString()
        remarkValue = binding.remarkEt.text.toString()


        var cancel = false
        var focusView: View? = null

        if (brandId == "") {
            focusView = binding.brandLayout
            binding.brandLayout.error = "Please choose any brand"
            cancel = true
        } else if (categoryId == "") {
            focusView = binding.categoryLayout
            binding.brandLayout.error = "Please choose any category"
            cancel = true
        } else if (problemType == "") {
            focusView = binding.problemLayout
            binding.problemLayout.error = "Please choose any problem type"
            cancel = true
        } else if (addValue == "") {
            focusView = binding.addressTil
            binding.addressTil.error = "Please enter address"
            cancel = true
        } else if (stateId == "") {
            focusView = binding.stateLayout
            binding.stateLayout.error = "Please choose any brand"
            cancel = true
        } else if (cityId == "") {
            focusView = binding.cityLayout
            binding.cityLayout.error = "Please choose any brand"
            cancel = true
        } else if (priorityType == "") {
            focusView = binding.priorityLayout
            binding.priorityLayout.error = "Please choose any brand"
            cancel = true
        } else if (remarkValue == "") {
            focusView = binding.remarkTil
            binding.remarkTil.error = "Please choose any brand"
            cancel = true
        } else if (base64Image == "") {
            focusView = binding.addImgLayout
            Toast.makeText(requireContext(), "Please upload image", Toast.LENGTH_SHORT).show()
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
        } else {
            startLoading("Please wait while getting serviceman list...")
            getServiceManList(brandId = "1", categoryId = "2", cityId = "1")

        }

    }

    private fun getServiceManList(brandId: String, categoryId: String, cityId: String) {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(true)
        blackBlind.authToken(tokenValue)
        blackBlind.addParams("brands_id", brandId)
        blackBlind.addParams("service_req_catid", categoryId)
        blackBlind.addParams("city_id", cityId)
        blackBlind.requestUrl(ServerApi.TECHNICIAN_REQUEST)
        blackBlind.executeRequest(Request.Method.POST, object : VolleyCallback {
            override fun getResponse(response: String?) {
                val jsonObject = JSONObject(response.toString())
                if (jsonObject.has("data")) {
                    val dataString = jsonObject.get("data")
                    if (dataString is JSONObject) {
                        Toast.makeText(
                            requireContext(),
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (dataString is JSONArray) {
                        val gsonBuilder = GsonBuilder()
                        gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                        val gson = gsonBuilder.create()
//                        serviceman = gson.fromJson(
//                            response,
//                            Serviceman::class.java
//                        )
//                        responseMsg = serviceman!!.message
//                        if (serviceman!!.success && serviceman!!.status == 1) {
//                            servicemanArray = serviceman!!.data as ArrayList<ServicemanData>
//                            stopLoading()
//                            val bottomSheet = ServicemanFragment(
//                                brandId, categoryId, cityId, problemType,
//                                stateId, priorityType, remarkValue, base64Image, addValue, latitude,
//                                longitude, servicemanArray
//                            )
//                            bottomSheet.show(
//                                parentFragmentManager,
//                                "BrandsFragment"
//                            )
//                        } else {
//
//                            Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
//                        }


                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        jsonObject.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
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
        if (apiType.equals("Brands")) {
            brandArray.clear()
            blackBlind.requestUrl(ServerApi.BRAND_REQUEST)
        } else if (apiType.equals("Category")) {
            categoryArray.clear()
            blackBlind.requestUrl(ServerApi.CATEGORY_REQUEST)
        }

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
//
//                    for (i in 0 until brandCategory.data.size) {
//
//                    }

                    if (apiType.equals("Brands")) {
                        brandArray = brandCategory.data as ArrayList<BrandCategoryData>
                        val brandCategoryAdapter =
                            BrandCategoryAdapter(requireContext(), brandArray)
                        binding.chooseBrand.setAdapter(brandCategoryAdapter)
                    } else if (apiType.equals("Category")) {
                        categoryArray = brandCategory.data as ArrayList<BrandCategoryData>
                        val brandCategoryAdapter =
                            BrandCategoryAdapter(requireContext(), categoryArray)
                        binding.chooseCategory.setAdapter(brandCategoryAdapter)
                    }

                } else {
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getCityStateDetails(apiType: String) {
        val blackBlind = BlackBlind(requireContext())
        blackBlind.headersRequired(false)
        if (apiType.equals("State")) {
            stateArray.clear()
            blackBlind.requestUrl(ServerApi.GET_STATE_LIST)
        } else if (apiType.equals("City")) {
            cityArray.clear()
            blackBlind.requestUrl(ServerApi.GET_CITY_LIST)
            blackBlind.addParams("state_id", stateId)
        }
        blackBlind.executeRequest(Request.Method.POST, object : VolleyCallback {
            override fun getResponse(response: String?) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.setDateFormat("M/d/yy hh:mm a")
                val gson = gsonBuilder.create()
                val stateCity = gson.fromJson(
                    response,
                    StateCity::class.java
                )
                responseMsg = stateCity.message
                if (stateCity.success && stateCity.status == 1) {
//                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
//
//                    for (i in 0 until stateCity.data.size) {
//
//                    }
                    if (apiType.equals("State")) {
                        stateArray = stateCity.data as ArrayList<StateCityData>
                        val stateCityAdapter = StateCityAdapter(requireContext(), stateArray)
                        binding.chooseState.setAdapter(stateCityAdapter)
                    } else if (apiType.equals("City")) {
                        cityArray = stateCity.data as ArrayList<StateCityData>
                        val stateCityAdapter = StateCityAdapter(requireContext(), cityArray)
                        binding.chooseCity.setAdapter(stateCityAdapter)
                    }


                } else {
                    Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun getError(error: String?) {
                Toast.makeText(requireContext(), responseMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun uploadFile() {
        photoPaths = java.util.ArrayList<String>()
        FilePickerBuilder.instance
            .setSelectedFiles(photoPaths)
            .setActivityTitle("Please select media")
            .enableVideoPicker(false)
            .enableCameraSupport(true)
            .showGifs(false)
            .showFolderView(false)
            .enableSelectAll(false)
            .enableImagePicker(true)
            .setCameraPlaceholder(R.drawable.camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickPhoto(this, CAMERA_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {

            if (CAMERA_REQUEST == requestCode && resultCode == Activity.RESULT_OK) {

                if (checkPermission()) {
                    photoPaths.addAll(data!!.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)!!)
                    val path = photoPaths[0]
                    var file = File(path)
                    try {
                        file = Compressor.Builder(context).setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.PNG).build()
                            .compressToFile(file)
                    } catch (e: Exception) {

                    }
                    var inputStream: InputStream? = null
//                InputStream inputStream = null;//You can get an inputStream using any IO API
                    inputStream = FileInputStream(file.absolutePath);
                    val size = file.length().toInt()
                    val buffer = ByteArray(size)
                    var bytesRead: Int
                    val output = ByteArrayOutputStream()
                    val output64 = Base64OutputStream(output, Base64.DEFAULT)
                    try {
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            output64.write(buffer, 0, bytesRead)
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            requireContext(),
                            "writ-err " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    output64.close()
                    base64Image = output.toString();
                    println("code = $base64Image")
                    binding.documentAddUploadTv.text = "Image Uploaded Successfully";
                    binding.ivAddLayout.visibility = View.GONE;
                    binding.documentAddUploadTv.setPadding(35, 50, 35, 50)
                    binding.documentAddUploadTv.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.irctc_check,
                        0
                    )
                } else {
                    requestPermission();
                }

            }

        } catch (e: Exception) {

        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        return !((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
                != PackageManager.PERMISSION_GRANTED))
    }

//    private fun checkPermission() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
//
//            // Requesting the permission
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
//        } else {
//            Toast.makeText( requireContext(), "Permission already granted", Toast.LENGTH_SHORT).show()
//        }
//    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>,
//                                            grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        } else if (requestCode == STORAGE_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}

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