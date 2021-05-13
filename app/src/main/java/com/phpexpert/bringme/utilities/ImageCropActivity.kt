package com.phpexpert.bringme.utilities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityImageCropBinding
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
class ImageCropActivity : BaseActivity() {
    private lateinit var mCropImageUri: Uri
    private lateinit var imageUri: Uri
    private var cropped1: Bitmap? = null
    private lateinit var imageCropActivityBinding: ActivityImageCropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageCropActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_image_crop)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        imageUri = Uri.parse(intent.getStringExtra("imageUri"))
        // Log.d("imageUri!!", String.valueOf(imageUri));
        setUriImages(imageUri)
        imageCropActivityBinding.rotateImage.setOnClickListener {
            imageCropActivityBinding.CropImageView.getCroppedImage(
                300,
                300,
                CropImageView.RequestSizeOptions.NONE
            )
            try { // Bitmap bitmap = MediaStore.Images.Media.getBitmap(ImageCropActivity.this.getContentResolver(), imageUri);
                val bitmap = MediaStore.Images.Media.getBitmap(
                    this@ImageCropActivity.contentResolver,
                    imageUri
                )
                val matrix = Matrix()
                matrix.postRotate(90f)
                val rotated = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
                imageCropActivityBinding.CropImageView.setImageBitmap(rotated)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setUriImages(imageUri: Uri?) {
        imageCropActivityBinding.CropImageView.setImageUriAsync(imageUri)
    }

    fun onCropImageClick(@Suppress("UNUSED_PARAMETER") view: View) {
        try {
            cropped1 = imageCropActivityBinding.CropImageView.getCroppedImage(
                300,
                300,
                CropImageView.RequestSizeOptions.NONE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (cropped1 != null) {
            try {
                val imgPath = saveBitmapIntoSDCardImage(cropped1!!)
                mCropImageUri = Uri.parse(imgPath.absolutePath)
                val intent = Intent()
                intent.putExtra("imgUri", mCropImageUri.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            } catch (e: Exception) {
                finish()
            }
        }
    }
}