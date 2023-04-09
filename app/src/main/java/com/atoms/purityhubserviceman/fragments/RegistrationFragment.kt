package com.atoms.purityhubserviceman.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.atoms.purityhubserviceman.*
import com.atoms.purityhubserviceman.adapter.BrandCategoryAdapter
import com.atoms.purityhubserviceman.adapter.StateCityAdapter
import com.atoms.purityhubserviceman.databinding.FragmentRegistationBinding
import com.atoms.purityhubserviceman.extra.Constants
import com.atoms.purityhubserviceman.extra.Constants.name
import com.atoms.purityhubserviceman.model.*
import com.google.gson.GsonBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
    var nameValue = ""
    var emailValue = ""
    var mobileValue = ""
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
        mobileNumber = arguments?.getString(Constants.mobile).toString()
        sharedpref = Sharedpref.getInstance(requireContext())
        tokenValue = sharedpref.getString(Constants.token)
//        checkPermission()

        binding.ivAddLayout.setOnClickListener {
//            uploadFile()
            requestPermissions();
        }

        binding.mobileEt.setText(mobileNumber.toString())

        binding.chooseState.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                stateId = stateArray[position].id.toString()
                getCityStateDetails(apiType = "City")
                binding.chooseState.setText(stateArray[position].name)
                val stateCityAdapter = StateCityAdapter(requireContext(), stateArray)
                binding.chooseState.setAdapter(stateCityAdapter)
                cityId = ""
                binding.chooseCity.text.clear()
            }
        binding.backImage.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.numberCheckFragment)

        }


        binding.chooseCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                cityId = cityArray[position].id.toString()

                binding.chooseCity.setText(cityArray[position].name)
                val stateCityAdapter = StateCityAdapter(requireContext(), cityArray)
                binding.chooseCity.setAdapter(stateCityAdapter)
            }

//        getBrandsDetails(apiType = "Brands")
        getCityStateDetails(apiType = "State")

        binding.registerBtn.setOnClickListener {
            validation()
        }
        return binding.root
    }

    private fun validation() {

        binding.stateLayout.error = null
        binding.cityLayout.error = null
        binding.addressTil.error = null
        binding.nameTil.error = null
        binding.emailTil.error = null
        binding.mobileTil.error = null
//        binding.addImgLayout.error = null

        nameValue = binding.nameEt.text.toString()
        emailValue = binding.emailEt.text.toString()
      mobileNumber = binding.mobileEt.text.toString()
        addValue = binding.addressEt.text.toString()
//        categoryId = binding.chooseCategory.text.toString()
//        problemType = binding.chooseProblemAt.text.toString()

//        stateId = binding.chooseState.text.toString()
//        cityId = binding.chooseCity.text.toString()
//        priorityType = binding.choosePriorityAt.text.toString()



        var cancel = false
        var focusView: View? = null

        if (nameValue == "") {
            focusView = binding.nameTil
            binding.nameTil.error = "Please enter your name"
            cancel = true
        } else if (emailValue == "") {
            focusView = binding.emailTil
            binding.emailTil.error = "Please enter your email"
            cancel = true
        } else if (mobileNumber == "" || mobileNumber.length < 10) {
            focusView = binding.mobileTil
            binding.mobileTil.error = "Please enter your mobile number"
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
        } else if (base64Image == "") {
            focusView = binding.addImgLayout
            Toast.makeText(requireContext(), "Please upload image", Toast.LENGTH_SHORT).show()
            cancel = true
        }

        if (cancel) {
            println("elseIF")
            focusView!!.requestFocus()
        } else {
            println("else")
            val bundle = bundleOf("nameValue" to nameValue,
                "emailValue" to emailValue, Constants.mobile to mobileNumber,
                "addValue" to addValue, "stateId" to stateId, "otpId" to otpId,
                "cityId" to cityId, "base64Image" to base64Image)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registationFragment_to_categoryFragment, bundle)

        }

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
            .setActivityTheme(R.style.Theme_PurityHubServiceman)
            .setCameraPlaceholder(R.drawable.camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickPhoto(this, CAMERA_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {

            if (CAMERA_REQUEST == requestCode && resultCode == Activity.RESULT_OK) {

//                if (checkPermission()) {
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
//                } else {
//                    requestPermission();
//                }

            }

        } catch (e: Exception) {

        }
    }

//    .withPermissions( Manifest.permission.CAMERA,
//    Manifest.permission.ACCESS_COARSE_LOCATION,
//    Manifest.permission.ACCESS_FINE_LOCATION,
//    Manifest.permission.READ_EXTERNAL_STORAGE,
//    Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun requestPermissions() {
        // below line is use to request permission in the current activity.
        // this method is use to handle error in runtime permissions
        Dexter.withContext(requireContext())
            // below line is use to request the number of permissions which are required in our app.
            .withPermissions( Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // after adding permissions we are calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                        uploadFile()
//                        Toast.makeText(requireContext(), "All the permissions are granted..", Toast.LENGTH_SHORT).show()
                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently, we will show user a dialog message.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    // this method is called when user grants some permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                // we are displaying a toast message for error message.
                Toast.makeText(requireContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            // below line is use to run the permissions on same thread and to check the permissions
            .onSameThread().check()
    }

    // below is the shoe setting dialog method
    // which is use to display a dialogue message.
    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = AlertDialog.Builder(requireContext())

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions")

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel()
            // below is the intent from which we are redirecting our user.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // this method is called when user click on negative button.
            dialog.cancel()
        }
        // below line is used to display our dialog
        builder.show()
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