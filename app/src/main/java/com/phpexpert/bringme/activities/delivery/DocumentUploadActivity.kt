package com.phpexpert.bringme.activities.delivery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.UploadDoucmentActivityBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.interfaces.PermissionInterface
import com.phpexpert.bringme.retro.ProfileRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
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

@Suppress("LocalVariableName", "DEPRECATION", "PrivatePropertyName")
class DocumentUploadActivity : BaseActivity(), AuthInterface, PermissionInterface {

    private lateinit var uploadDocumentActivity: UploadDoucmentActivityBinding
    private lateinit var permissionName: String
    private var POD1_URI: Uri? = null
    private var POD2_URI: Uri? = null
    private var permissionCamera = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var languageDtoData: LanguageDtoData
    private lateinit var imageUri: Uri

    @SuppressLint("ObsoleteSdkInt", "SetTextI18n")
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
        permissionInterface = this
        uploadDocumentActivity = DataBindingUtil.setContentView(this, R.layout.upload_doucment_activity)
        uploadDocumentActivity.languageModel = sharedPrefrenceManager.getLanguageData()

        uploadDocumentActivity.documentType.text = "${intent.extras?.getString("document_type")} ${sharedPrefrenceManager.getLanguageData().document}"
        languageDtoData = sharedPrefrenceManager.getLanguageData()

        uploadDocumentActivity.backArrow.setOnClickListener {
            finish()
        }

        if (sharedPrefrenceManager.getProfile().login_document_front_photo != "") {
            uploadDocumentActivity.documentFrontLayoutUpload.visibility = View.GONE
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(this).load(sharedPrefrenceManager.getProfile().login_document_front_photo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into(uploadDocumentActivity.imageViewFront)
            uploadDocumentActivity.documentStatus.text = sharedPrefrenceManager.getProfile().document_status
        }
        if (sharedPrefrenceManager.getProfile().login_document_back_photo != "") {
            uploadDocumentActivity.documentBackLayoutUpload.visibility = View.GONE
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(this).load(sharedPrefrenceManager.getProfile().login_document_back_photo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into(uploadDocumentActivity.imageViewBack)
        }

        if (sharedPrefrenceManager.getProfile().document_status == "1") {
            uploadDocumentActivity.documentFrontLayout.isFocusable = false
            uploadDocumentActivity.documentFrontLayout.isClickable = false
            uploadDocumentActivity.documentFrontLayout.isFocusableInTouchMode = false

            uploadDocumentActivity.documentBackLayout.isFocusable = false
            uploadDocumentActivity.documentBackLayout.isClickable = false
            uploadDocumentActivity.documentBackLayout.isFocusableInTouchMode = false

            uploadDocumentActivity.editIcon.visibility = View.GONE
            uploadDocumentActivity.submitData.visibility = View.GONE
            uploadDocumentActivity.uploadDocumentText.visibility = View.GONE

            uploadDocumentActivity.contactCustomerSupport.visibility = View.VISIBLE
        }

        setAction()

    }

    private fun setAction() {
        uploadDocumentActivity.documentFrontLayout.setOnClickListener {
            permissionName = "Front"
            if (isCheckPermissions(this, permissionCamera))
                startDialog()
        }

        uploadDocumentActivity.documentBackLayout.setOnClickListener {
            permissionName = "Back"
            if (isCheckPermissions(this, permissionCamera))
                startDialog()
        }

        uploadDocumentActivity.submitData.setOnClickListener {
            if (POD1_URI != null || POD2_URI != null) {
                uploadDocumentActivity.submitData.startAnimation()
                editImageData()
            } else {
                finish()
            }
        }

        uploadDocumentActivity.editIcon.setOnClickListener {
            val i = Intent(this, UploadDocumentSelectActivity::class.java)
            i.putExtra("page", "edit")
            startActivity(i)
            finish()
        }
    }

    @SuppressLint("CutPasteId")
    private fun startDialog() {
        val bottomSheetDialog1 = BottomSheetDialog(this, R.style.SheetDialog)
        bottomSheetDialog1.setContentView(R.layout.bottom__chooser_dialog_layout)
        bottomSheetDialog1.setCancelable(true)
        bottomSheetDialog1.findViewById<TextView>(R.id.textHeading)?.text = sharedPrefrenceManager.getLanguageData().alert_text
        bottomSheetDialog1.findViewById<ImageView>(R.id.closeIcon)?.setOnClickListener {
            bottomSheetDialog1.dismiss()
        }
        val bottomSheetDialogMessageText = bottomSheetDialog1.findViewById<TextView>(R.id.message)!!
        val bottomSheetDialogMessageOkButton = bottomSheetDialog1.findViewById<TextView>(R.id.okText)!!
        val bottomSheetDialogMessageCancelButton = bottomSheetDialog1.findViewById<TextView>(R.id.cancelText)!!
        bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().how_do_you_want_to_set_your_picture
        bottomSheetDialogMessageOkButton.text = languageDtoData.gallery_text
        bottomSheetDialogMessageCancelButton.text = languageDtoData.camera_text

        bottomSheetDialogMessageOkButton.setOnClickListener {
            val pictureActionIntent: Intent?
            pictureActionIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(
                    pictureActionIntent,
                    4001)
            bottomSheetDialog1.dismiss()

        }
        bottomSheetDialogMessageCancelButton.setOnClickListener {
            val intent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE)
//            val outputFileUri: Uri? = getCaptureImageOutputUri()!!
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
            imageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )!!
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    imageUri)
            startActivityForResult(intent,
                    4001)
            bottomSheetDialog1.dismiss()
        }
        bottomSheetDialog1.show()
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
                if (permissionName == "Front") {
                    POD1_URI = Uri.parse(data!!.getStringExtra("imgUri"))
                    val selectedImage = BitmapFactory.decodeFile(POD1_URI.toString()) as Bitmap
                    var requestOptions = RequestOptions()
                    uploadDocumentActivity.documentFrontLayoutUpload.visibility = View.GONE
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
                    Glide.with(this).load(selectedImage)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .apply(requestOptions)
                            .into(uploadDocumentActivity.imageViewFront)
                } else {
                    POD2_URI = Uri.parse(data!!.getStringExtra("imgUri"))
                    val selectedImage = BitmapFactory.decodeFile(POD2_URI.toString()) as Bitmap
                    var requestOptions = RequestOptions()
                    uploadDocumentActivity.documentBackLayoutUpload.visibility = View.GONE
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
                    Glide.with(this).load(selectedImage)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .apply(requestOptions)
                            .into(uploadDocumentActivity.imageViewBack)
                }
            }
        }
    }

    private fun getCaptureImageOutputUri(): Uri {
        /*var outputFileUri: Uri? = null
        val getImage = getExternalFilesDir("")
        if (getImage != null) {
            outputFileUri =
                    Uri.fromFile(File(Environment
                            .getExternalStorageDirectory(), "temp.jpg"))
        }*/
        return imageUri
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
                getPathFromURI(imageUri)
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

    private fun editImageData() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != "" && sharedPrefrenceManager.getAuthData()?.auth_key != null) {
                val map = HashMap<String, RequestBody?>()
                map["vendor_id"] = createRequestBody(sharedPrefrenceManager.getLoginId())
                map["document_country"] = createRequestBody(/*base64Encoded(*/intent.extras?.getString("document_country")!!)
                map["document_type"] = when (intent.extras?.getString("document_type")) {
                    sharedPrefrenceManager.getLanguageData().id_card -> {
                        createRequestBody("id_card")
                    }
                    sharedPrefrenceManager.getLanguageData().passport -> {
                        createRequestBody("passport")
                    }
                    else -> {
                        createRequestBody("driving_license")
                    }
                }

                map["auth_key"] = createRequestBody(sharedPrefrenceManager.getAuthData()?.auth_key!!)
                map["lang_code"] = createRequestBody(sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!)
                ServiceGenerator.createService(ProfileRetro::class.java)
                        .uploadDocument(map, createMultiPartBody(POD1_URI, "document_front"),
                                createMultiPartBody(POD2_URI, "document_back"))
                        .enqueue(object : Callback<JsonObject> {
                            @SuppressLint("SetTextI18n")
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                if (response.isSuccessful) {
                                    uploadDocumentActivity.submitData.revertAnimation()
                                    val responseData = response.body()
                                    if (responseData?.get("status_code")?.asString == "2") {
                                        hitAuthApi(this@DocumentUploadActivity)
                                    } else {
                                        bottomSheetDialogHeadingText.visibility = View.VISIBLE
                                        when (responseData?.get("status_code")?.asString) {
                                            "0" -> {
                                                bottomSheetDialogHeadingText.visibility = View.GONE
                                            }
                                            else -> bottomSheetDialogHeadingText.visibility = View.VISIBLE
                                        }
                                        bottomSheetDialogMessageText.text = responseData?.get("status_message")?.asString
                                        bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                                        bottomSheetDialogMessageCancelButton.visibility = View.GONE
                                        bottomSheetDialogMessageOkButton.setOnClickListener {
                                            bottomSheetDialog.dismiss()
                                            sharedPrefrenceManager.getProfile().document_type = when (intent.extras?.getString("document_type")) {
                                                sharedPrefrenceManager.getLanguageData().id_card -> {
                                                    "id_card"
                                                }
                                                sharedPrefrenceManager.getLanguageData().passport -> {
                                                    "passport"
                                                }
                                                else -> {
                                                    "driving_license"
                                                }
                                            }
                                            sharedPrefrenceManager.getProfile().login_document_front_photo = responseData?.get("data")?.asJsonObject?.get("login_document_front_photo")?.asString
                                            sharedPrefrenceManager.getProfile().login_document_back_photo = responseData?.get("data")?.asJsonObject?.get("login_document_back_photo")?.asString
                                            sharedPrefrenceManager.getProfile().document_country = responseData?.get("data")?.asJsonObject?.get("document_country")?.asString
                                            sharedPrefrenceManager.getProfile().document_status = responseData?.get("data")?.asJsonObject?.get("document_status")?.asString
                                            finish()
                                            /*if (responseData?.status_code == "0")
                                            finish()*/
                                        }
                                        bottomSheetDialog.show()
                                    }
                                } else {
                                    uploadDocumentActivity.submitData.revertAnimation()
                                    bottomSheetDialogMessageText.text = languageDtoData.could_not_connect_server_message
                                    bottomSheetDialogHeadingText.visibility = View.GONE
                                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                                    bottomSheetDialogMessageOkButton.setOnClickListener {
                                        bottomSheetDialog.dismiss()
                                    }
                                    bottomSheetDialog.show()
                                }

                            }

                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                uploadDocumentActivity.submitData.revertAnimation()
                                bottomSheetDialogMessageText.text = languageDtoData.could_not_connect_server_message
                                bottomSheetDialogHeadingText.visibility = View.VISIBLE
                                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                                bottomSheetDialogMessageOkButton.setOnClickListener {
                                    bottomSheetDialog.dismiss()
                                }
                                bottomSheetDialog.show()
                            }

                        })
            } else hitAuthApi(this)
        } else {
            uploadDocumentActivity.submitData.revertAnimation()
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
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

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            editImageData()
        } else {
            uploadDocumentActivity.submitData.revertAnimation()
            bottomSheetDialogMessageText.text = message
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    override fun isPermission(value: Boolean) {
        if (value) {
            startDialog()
        }
    }
}