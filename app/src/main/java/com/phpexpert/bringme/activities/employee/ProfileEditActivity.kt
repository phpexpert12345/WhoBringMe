package com.phpexpert.bringme.activities.employee

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ProfileEditLayoutBinding
import com.phpexpert.bringme.dtos.*
import com.phpexpert.bringme.retro.ProfileRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import com.phpexpert.bringme.retro.ServiceGeneratorLocation
import com.phpexpert.bringme.ui.employee.profile.ProfileViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.ImageCropActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.reflect.Method
import java.net.URI
import java.util.*


@Suppress("LocalVariableName", "DEPRECATION", "PrivatePropertyName", "SameParameterValue")
class ProfileEditActivity : BaseActivity() {
    private lateinit var profileEditLayoutBinding: ProfileEditLayoutBinding
    private var mResultList = ArrayList<PlaceAutocomplete>()
    private val arrayList = ArrayList<String>()
    private var POD1_URI: Uri? = null
    private val postDataOtp = PostDataOtp()
    private lateinit var languageDtoData: LanguageDtoData

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        profileEditLayoutBinding = DataBindingUtil.setContentView(this, R.layout.profile_edit_layout)
        profileEditLayoutBinding.languageModel = sharedPrefrenceManager.getLanguageData()
        languageDtoData = sharedPrefrenceManager.getLanguageData()
        profileEditLayoutBinding.autoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                getPredictions(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        profileEditLayoutBinding.backArrow.setOnClickListener {
            finish()
        }
        isCheckPermissions(this, perission)
        profileEditLayoutBinding.autoComplete.setOnItemClickListener { _, _, i, _ ->

            val geocoder = Geocoder(this@ProfileEditActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(mResultList[i].geometry!!.location!!.lat!!.toDouble(), mResultList[i].geometry!!.location!!.lng!!.toDouble(), 1)
            postDataOtp.accountLat = mResultList[i].geometry!!.location!!.lat
            postDataOtp.accountLong = mResultList[i].geometry!!.location!!.lng
            postDataOtp.accountCountry = addresses[0]!!.countryName
            profileEditLayoutBinding.stateEt.text = Editable.Factory.getInstance().newEditable(addresses[0]!!.adminArea)
            profileEditLayoutBinding.cityET.text = Editable.Factory.getInstance().newEditable(addresses[0]!!.locality)
            profileEditLayoutBinding.postCodeEt.text = Editable.Factory.getInstance().newEditable(addresses[0]!!.postalCode)
            postDataOtp.accountState = addresses[0]!!.adminArea
            postDataOtp.accountCity = addresses[0]!!.locality
            val stringBuilder = StringBuilder()
            for (j in 0..addresses[0]!!.maxAddressLineIndex)
                stringBuilder.append(addresses[0]!!.getAddressLine(j) + ",")
            postDataOtp.accountAddress = stringBuilder.toString()
            postDataOtp.addressPostCode = addresses[0]!!.postalCode
        }

        profileEditLayoutBinding.editProfile.setOnClickListener {
            startActivityForResult(getPickImageChooserIntent(), 4001)
        }

        profileEditLayoutBinding.updateButton.setOnClickListener {
            if (isOnline()) {
                if (checkValidations() && isCheckPermissions(this, perission))
                    editImageData()
            } else{
                bottomSheetDialogMessageText.text = languageDtoData.network_error
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        }
        setValues()
    }

    private fun setValues() {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
        Glide.with(this).load(sharedPrefrenceManager.getProfile().login_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .placeholder(R.drawable.user_placeholder)
                .into(profileEditLayoutBinding.userImage)

        profileEditLayoutBinding.firstNameEt.text = Editable.Factory.getInstance().newEditable(sharedPrefrenceManager.getProfile().login_name!!.split(" ")[0])
        try {
            profileEditLayoutBinding.lastName.text = Editable.Factory.getInstance().newEditable(sharedPrefrenceManager.getProfile().login_name!!.split(" ")[1])
        } catch (e: Exception) {
            e.printStackTrace()
        }
        profileEditLayoutBinding.emailEt.text = Editable.Factory.getInstance().newEditable(sharedPrefrenceManager.getProfile().login_email!!)
        profileEditLayoutBinding.autoComplete.text = Editable.Factory.getInstance().newEditable(sharedPrefrenceManager.getProfile().login_address!!)
        profileEditLayoutBinding.stateEt.text = Editable.Factory.getInstance().newEditable(sharedPrefrenceManager.getProfile().login_state!!)
        profileEditLayoutBinding.cityET.text = Editable.Factory.getInstance().newEditable(sharedPrefrenceManager.getProfile().login_city!!)
        profileEditLayoutBinding.postCodeEt.text = Editable.Factory.getInstance().newEditable(sharedPrefrenceManager.getProfile().login_postcode!!)

        postDataOtp.accountLat = sharedPrefrenceManager.getProfile().login_lat
        postDataOtp.accountLong = sharedPrefrenceManager.getProfile().login_long
        postDataOtp.accountCountry = sharedPrefrenceManager.getProfile().login_country
        postDataOtp.accountState = sharedPrefrenceManager.getProfile().login_state
        postDataOtp.accountCity = sharedPrefrenceManager.getProfile().login_city
        postDataOtp.accountAddress = sharedPrefrenceManager.getProfile().login_address
        postDataOtp.addressPostCode = sharedPrefrenceManager.getProfile().login_postcode
    }

    private fun checkValidations(): Boolean {
        return when {
            profileEditLayoutBinding.firstNameEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.enter_first_name
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            profileEditLayoutBinding.lastName.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.enter_last_name
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            profileEditLayoutBinding.emailEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.enter_email
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            !profileEditLayoutBinding.emailEt.text.toString().isValidEmail() -> {
                bottomSheetDialogMessageText.text = languageDtoData.enater_valid_email
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()

                false
            }
            profileEditLayoutBinding.autoComplete.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.entera_address
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            profileEditLayoutBinding.stateEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.enter_state
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            profileEditLayoutBinding.cityET.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.enter_city
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            profileEditLayoutBinding.postCodeEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.enter_post_code
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            else -> {
                true
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getPickImageChooserIntent(): Intent? {
        val outputFileUri: Uri? = getCaptureImageOutputUri()!!
        val allIntents: ArrayList<Intent> = ArrayList()
        val packageManager = packageManager
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam =
                packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery =
                packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }
        var mainIntent: Intent? = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)
        val chooserIntent = Intent.createChooser(mainIntent, languageDtoData.select_ource)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())
        return chooserIntent
    }

    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage = getExternalFilesDir("")
        if (getImage != null) {
            outputFileUri =
                    Uri.fromFile(File(getImage.path, "profile.png"))
        }
        return outputFileUri
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 4001) {
                val filePath = getImageFilePath(data)
                val imageUri = getPickImageResultUri(data)
                if (filePath != null) {
                    val intent = Intent(
                            this,
                            ImageCropActivity::class.java
                    )
                    intent.putExtra("imageUri", imageUri.toString())
                    startActivityForResult(intent, 999)
                }
            } else if (requestCode == 999) {
                Toast.makeText(this, "Ok", Toast.LENGTH_LONG).show()
                POD1_URI = Uri.parse(data!!.getStringExtra("imgUri"))
                val selectedImage = BitmapFactory.decodeFile(POD1_URI.toString()) as Bitmap
                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
                Glide.with(this).load(selectedImage)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .apply(requestOptions)
                        .into(profileEditLayoutBinding.userImage)
            }
        }
    }

    private fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.data
    }

    private fun getImageFilePath(data: Intent?): String? {
        return getImageFromFilePath(data)
    }

    private fun getImageFromFilePath(data: Intent?): String? {
        val isCamera = data?.data == null
        return try {
            if (isCamera) {
                getCaptureImageOutputUri()!!.path!!
            } else {
                getPathFromURI(data?.data!!)!!
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //            Toast.makeText(requireActivity(), "Wrong File Selected", Toast.LENGTH_LONG).show()
            ""
        }
    }

    @SuppressLint("Recycle")
    private fun getPathFromURI(contentUri: Uri?): String? {
        return try {
            val proj = arrayOf(
                    MediaStore.Audio.Media.DATA
            )
            val cursor =
                    contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } catch (e: Exception) {
            contentUri!!.path!!
        }
    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    override fun onPause() {
        super.onPause()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

    private fun getPredictions(Query: String): ArrayList<PlaceAutocomplete> {
        val results: ArrayList<PlaceAutocomplete> = ArrayList<PlaceAutocomplete>()
        ServiceGeneratorLocation.createService(ProfileRetro::class.java).getPlaces(Query, sharedPrefrenceManager.getAuthData().GOOGLE_MAP_KEY).enqueue(object : Callback<PlaceMainClass> {
            override fun onResponse(call: Call<PlaceMainClass>, response: Response<PlaceMainClass>) {
                mResultList.clear()
                mResultList.addAll(response.body()!!.results)
                arrayList.clear()
                for (i in mResultList.indices) {
                    arrayList.add(mResultList[i].formatted_address!!)
                }
                //                mPlaceArrayAdapter =
//                ;
                val adapter: ArrayAdapter<*> = ArrayAdapter(this@ProfileEditActivity, android.R.layout.simple_dropdown_item_1line, arrayList)
                profileEditLayoutBinding.autoComplete.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<PlaceMainClass>, t: Throwable) {}
        })
        return results
    }

    private fun editImageData() {
        val map = HashMap<String, RequestBody?>()
        map["account_first_name"] = createRequestBody(base64Encoded(profileEditLayoutBinding.firstNameEt.text.toString()))
        map["account_last_name"] = createRequestBody(base64Encoded(profileEditLayoutBinding.lastName.text.toString()))
        map["LoginId"] = createRequestBody(sharedPrefrenceManager.getLoginId())
        map["account_email"] = createRequestBody((profileEditLayoutBinding.emailEt.text.toString()))
        map["account_country"] = createRequestBody(base64Encoded(postDataOtp.accountCountry!!))
        map["account_state"] = createRequestBody(base64Encoded(profileEditLayoutBinding.stateEt.text.toString()))
        map["account_city"] = createRequestBody(base64Encoded(profileEditLayoutBinding.cityET.text.toString()))
        map["account_address"] = createRequestBody(base64Encoded(profileEditLayoutBinding.autoComplete.text.toString()))
        map["address_postcode"] = createRequestBody(base64Encoded(profileEditLayoutBinding.postCodeEt.text.toString()))
        map["account_lat"] = createRequestBody(postDataOtp.accountLat!!)
        map["account_long"] = createRequestBody(postDataOtp.accountLong!!)
        map["auth_key"] = createRequestBody(sharedPrefrenceManager.getAuthData().auth_key!!)
        map["lang_code"] = createRequestBody(sharedPrefrenceManager.getAuthData().lang_code!!)
        ServiceGenerator.createService(ProfileRetro::class.java)
                .editPhotoData(map, createMultiPartBody(POD1_URI, "account_photo"))
                .enqueue(object : Callback<EditProfileDto> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<EditProfileDto>, response: Response<EditProfileDto>) {
                        if (response.isSuccessful) {
                            val responseData = response.body()
                            bottomSheetDialogMessageText.text = responseData!!.status_message
                            bottomSheetDialogMessageOkButton.text = "Ok"
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                                if (responseData.status_code == "0") {
                                    val loginData = sharedPrefrenceManager.getProfile()
                                    loginData.login_photo = ""
                                    loginData.login_email = profileEditLayoutBinding.emailEt.text.toString()
                                    loginData.login_name = profileEditLayoutBinding.firstNameEt.text.toString() + " " + profileEditLayoutBinding.lastName.text.toString()
                                    loginData.login_address = profileEditLayoutBinding.autoComplete.text.toString()
                                    loginData.login_country = postDataOtp.accountCountry
                                    loginData.login_city = postDataOtp.accountCity
                                    loginData.login_state = postDataOtp.accountState
                                    loginData.login_postcode = postDataOtp.addressPostCode
                                    loginData.login_lat = postDataOtp.accountLat
                                    loginData.login_long = postDataOtp.accountLong
                                    sharedPrefrenceManager.saveProfile(loginData)
                                    ProfileViewModel.changeModel.postValue(true)
                                    finish()
                                }
                            }
                            bottomSheetDialog.show()
                        } else {
                            bottomSheetDialogMessageText.text = languageDtoData.edit_profile_api_error
                            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                            bottomSheetDialog.show()
                        }
                    }

                    override fun onFailure(call: Call<EditProfileDto>, t: Throwable) {
                        bottomSheetDialogMessageText.text = languageDtoData.edit_profile_api_error
                        bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                        bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        bottomSheetDialogMessageOkButton.setOnClickListener {
                            bottomSheetDialog.dismiss()
                        }
                        bottomSheetDialog.show()
                    }

                })
    }

    private fun createRequestBody(string: String): RequestBody? {
        return if (string != "") {
            RequestBody.create(
                    MediaType.parse("text/plain"),
                    string
            )
        } else null
    }

    private fun createMultiPartBody(uri: Uri?, imageName: String): MultipartBody.Part? {
        return if (uri != null) {
            val file = File(URI("file://" + uri.path!!.replace(" ", "%20")))
            val requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part.createFormData(imageName, file.name, requestFile)
        } else {
            null
        }
    }
}