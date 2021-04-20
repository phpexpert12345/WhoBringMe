package com.phpexpert.bringme.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "unused", "DEPRECATION", "LocalVariableName", "MemberVisibilityCanBePrivate", "NAME_SHADOWING", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ImagePicker(private val mContext: Context) {
    interface ImagePickerDialogListener {
        fun onCameraSelected()
        fun onGallerySelected()
        fun onDeleteSelected()
    }

    @Throws(NullPointerException::class)
    fun openGalleryForOneImageSelection(fragment: Fragment?, RequestCode: Int) {
        if (fragment == null) {
            //fragment is null. startActivityForResult cannot be called.
            throw NullPointerException("fragment is null. startActivityForResult cannot be called.")
        }
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        fragment.startActivityForResult(Intent.createChooser(intent, "Choose File"), RequestCode)
    }

    @Throws(NullPointerException::class)
    fun openGalleryForMultipleImageSelection(fragment: Fragment?, RequestCode: Int) {
        if (fragment == null) {
            //fragment is null. startActivityForResult cannot be called.
            throw NullPointerException("fragment is null. startActivityForResult cannot be called.")
        }
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK

        //enabling for multiple image selection. NOTE: Only works on API level 18 and above.
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        //Starting Gallery for getting the images
        fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), RequestCode)
    }

    fun openCameraForImageThumbnailCapturing(fragment: Fragment, RequestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(intent, RequestCode)
    }

    fun openCameraForImageCapturing(fragment: Fragment, requestCode: Int, directory: String?, prefix: String?, extension: String?): Uri {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photo: File? = null
        try {
            // place where to store camera taken picture
            photo = createFile(directory, prefix, extension)
            photo.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            //            Log.v(TAG, "Can't create file to take picture!");
//            Toast.makeText(activity, "Please check SD card! Image shot is impossible!", 10000);
//            return false;
        }
        val mImageUri: Uri = Uri.fromFile(photo)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        fragment.startActivityForResult(intent, requestCode)
        Log.e("IMAGE URI", "" + mImageUri.toString())
        return mImageUri
    }

    @Throws(NullPointerException::class)
    fun processCameraImageWithoutCompression(data: Intent?): Bitmap? {
        if (data == null) {
            throw NullPointerException("parameter (Intent data) is null.")
        }
        return try {
            data.extras!!["data"] as Bitmap?
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Throws(NullPointerException::class)
    fun processCameraImageWithoutCompression(context: Context, uri: Uri?): Bitmap? {
        if (uri == null) {
            throw NullPointerException("parameter uri is null.")
        }

        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Throws(NullPointerException::class)
    fun processCameraImageWithCompression(data: Intent?, compressionPercentage: Int): Bitmap? {
        if (data == null) {
            throw NullPointerException("parameter (Intent data) is null.")
        }
        try {
            var bm = data.extras!!["data"] as Bitmap?
            bm = Bitmap.createScaledBitmap((bm)!!, bm.width / 2, bm.height / 2, true)
            val imageFileName = getFilename(null)
            val fileOutputStream = FileOutputStream(imageFileName)
            bm.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, fileOutputStream)
            bm.recycle()
            val compressedBitmap = BitmapFactory.decodeFile(imageFileName)

            //delete the file.
            val file = File(imageFileName)
            file.delete()
            return compressedBitmap
        } catch (e: IllegalArgumentException) {
            if (compressionPercentage < 0 || compressionPercentage > 100) {
                throw IllegalArgumentException("compression percentage must be 0..100")
            } else {
                e.printStackTrace()
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @Throws(NullPointerException::class)
    fun processCameraImageWithCompression(uri: File?, compressionPercentage: Int): Bitmap? {
        if (uri == null) {
            throw NullPointerException("parameter uri is null.")
        }
        try {
            var bm = BitmapFactory.decodeFile(uri.absolutePath)
            bm = Bitmap.createScaledBitmap((bm)!!, bm.width / 2, bm.height / 2, true)
            val imageFileName = getFilename(null)
            val fileOutputStream = FileOutputStream(imageFileName)
            bm.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, fileOutputStream)
            bm.recycle()
            var compressedBitmap = BitmapFactory.decodeFile(imageFileName)

            //delete the file.
            val file = File(imageFileName)
            file.delete()
            try {
                val exifInterface = ExifInterface(uri.path)
                when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> compressedBitmap = rotateImage(compressedBitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> compressedBitmap = rotateImage(compressedBitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> compressedBitmap = rotateImage(compressedBitmap, 270f)
                    ExifInterface.ORIENTATION_NORMAL -> {
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return compressedBitmap
        } catch (e: IllegalArgumentException) {
            if (compressionPercentage < 0 || compressionPercentage > 100) {
                throw IllegalArgumentException("compression percentage must be 0..100")
            } else {
                e.printStackTrace()
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @Throws(NullPointerException::class)
    fun processGalleryImageWithCompression(data: Intent?, requiredSize: Int, minimumScaleFactor: Int, compressionPercentage: Int): Bitmap? {
        if (data == null) {
            throw NullPointerException("parameter (Intent data) is null.")
        }
        try {
            val selectedImage = data.data
            //just getting bounds for the image.
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(mContext.contentResolver.openInputStream((selectedImage)!!), null, o)

            /*Scaling factor processing start.*/
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = minimumScaleFactor
            while (true) {
                if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize) {
                    break
                }
                width_tmp /= 2
                height_tmp /= 2
                scale++
                //Scaling down
            }
            /*Scaling factor processing end.*/

            //Getting image as scaled image.
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val bitmap = BitmapFactory.decodeStream(mContext.contentResolver.openInputStream((selectedImage)), null, o2)

            //Creating output file to save compressed version.
            val imageFileName = getFilename(null)
            val fileOutputStream = FileOutputStream(imageFileName)

            //Compressing bitmap to 50%.
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, fileOutputStream)
            bitmap.recycle()
            val compressedBitmap = BitmapFactory.decodeFile(imageFileName)

            //delete the file.
            val file = File(imageFileName)
            file.delete()
            return compressedBitmap
        } catch (e: IllegalArgumentException) {
            if (compressionPercentage < 0 || compressionPercentage > 100) {
                throw IllegalArgumentException("compression percentage must be 0..100")
            } else {
                e.printStackTrace()
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun performCrop(activity: Activity, picUri: Uri?): Uri? {
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*")
            // set crop properties
            cropIntent.putExtra("crop", "true")
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            // indicate output X and Y
            // retrieve data on return
            cropIntent.putExtra("return-data", true)
            val f = File(Environment.getExternalStorageDirectory(),
                    "/temporary_holder.jpg")
            try {
                f.createNewFile()
            } catch (ex: IOException) {
                Log.e("io", (ex.message)!!)
            }
            val uri = Uri.fromFile(f)
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            // start the activity - we handle returning in onActivityResult
            activity.startActivityForResult(cropIntent, 99)
            return uri
        } // respond to users whose devices do not support the crop action
        catch (anfe: ActivityNotFoundException) {
            // display an error message
            val errorMessage = "Whoops - your device doesn't support the crop action!"
            val toast = Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT)
            toast.show()
            return null
        }
    }

    fun getFilename(locationForFiles: String?): String {
        val file: File = if (locationForFiles == null || locationForFiles.isEmpty()) {
            File(Environment.getExternalStorageDirectory().path, DEFAULT_FILE_LOCATION) //if No name is provided.
        } else {
            File(Environment.getExternalStorageDirectory().path, locationForFiles)
        }
        if (!file.exists()) {
            file.mkdirs() //creating folder if not already exist.
        }

        //generating file name including folder name
        return (file.absolutePath + "/" + System.currentTimeMillis() + ".jpg")
    }

    @Throws(IOException::class)
    fun createFile(locationForFiles: String?, prefix: String?, extensionName: String?): File {
        var locationForFiles = locationForFiles
        var prefix = prefix
        var extensionName = extensionName
        if (locationForFiles == null || locationForFiles.isEmpty()) {
            locationForFiles = mContext.cacheDir.absolutePath //if No name is provided.
        }
        if (prefix == null || prefix.isEmpty()) {
            prefix = DEFAULT_PREFIX //if No name is provided.
        }
        if (extensionName == null || extensionName.isEmpty()) {
            extensionName = DEFAULT_EXTENSION //if No extension is provided.
        }
        val file = File(Environment.getExternalStorageDirectory().absolutePath, "/$locationForFiles")
        if (!file.exists()) {
            file.mkdirs() //creating folder if not already exist.
        }
        return File.createTempFile(prefix, extensionName, file)
    }

    @Throws(Exception::class)
    fun createTemporaryFile(part: String?, ext: String?): File {
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.absolutePath + "/.temp/")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(part, ext, tempDir)
    }

    companion object {
        const val DEFAULT_FILE_LOCATION = "Burst/JPG/"
        const val DEFAULT_PREFIX = "Picture"
        const val DEFAULT_EXTENSION = ".jpg"
        @SuppressLint("StaticFieldLeak")
        private var singleInstance: ImagePicker? = null
        fun getInstance(context: Context): ImagePicker? {
            if (singleInstance == null) {
                singleInstance = ImagePicker(context)
            }
            return singleInstance
        }

        fun openCameraGalleryChooserDialog(activity: Activity?, imagePickerDialogListener: ImagePickerDialogListener?): Boolean {
            val items = arrayOf("Camera", "Gallery", "Remove picture")
            try {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Select source")
                builder.setItems(items) { _, which ->
                    when (which) {
                        0 -> imagePickerDialogListener?.onCameraSelected()
                        1 -> imagePickerDialogListener?.onGallerySelected()
                        2 -> imagePickerDialogListener?.onDeleteSelected()
                    }
                }
                builder.create().show()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        fun openCameraGalleryChooser(activity: Activity?, imagePickerDialogListener: ImagePickerDialogListener?): Boolean {
            val items = arrayOf("Camera", "Gallery")
            return try {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Select source")
                builder.setItems(items) { _, which ->
                    when (which) {
                        0 -> imagePickerDialogListener?.onCameraSelected()
                        1 -> imagePickerDialogListener?.onGallerySelected()
                    }
                }
                builder.create().show()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix,
                    true)
        }

        private const val DEFAULT_MIN_WIDTH_QUALITY = 400 // min pixels
        private const val TAG = "ImagePicker"
        private const val TEMP_IMAGE_NAME = "tempImage"
        var minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY
        fun getImageFromResult(context: Context, resultCode: Int,
                               imageReturnedIntent: Intent?): Bitmap? {
            Log.d(TAG, "getImageFromResult, resultCode: $resultCode")
            var bm: Bitmap? = null
            val imageFile = getTempFile(context)
            if (resultCode == Activity.RESULT_OK) {
                val selectedImage: Uri?
                val isCamera = (((imageReturnedIntent == null) || (
                        imageReturnedIntent.data == null) || (imageReturnedIntent.data == Uri.fromFile(imageFile))))
                selectedImage = if (isCamera) {
                    /** CAMERA  */
                    Uri.fromFile(imageFile)
                } else {
                    /** ALBUM  */
                    imageReturnedIntent!!.data
                }
                Log.d(TAG, "selectedImage: $selectedImage")
                bm = getImageResized(context, selectedImage)
                val rotation = getRotation(context, selectedImage)
                bm = rotate(bm, rotation)
            }
            return bm
        }

        private fun decodeBitmap(context: Context, theUri: Uri?, sampleSize: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inSampleSize = sampleSize
            var fileDescriptor: AssetFileDescriptor? = null
            try {
                fileDescriptor = context.contentResolver.openAssetFileDescriptor((theUri)!!, "r")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor!!.fileDescriptor, null, options)
            Log.d(TAG, (options.inSampleSize.toString() + " sample method bitmap ... " +
                    actuallyUsableBitmap.width + " " + actuallyUsableBitmap.height))
            return actuallyUsableBitmap
        }

        /**
         * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
         */
        private fun getImageResized(context: Context, selectedImage: Uri?): Bitmap {
            var bm: Bitmap?
            val sampleSizes = intArrayOf(5, 3, 2, 1)
            var i = 0
            do {
                bm = decodeBitmap(context, selectedImage, sampleSizes[i])
                Log.d(TAG, "resizer: new bitmap width = " + bm.width)
                i++
            } while (bm!!.width < minWidthQuality && i < sampleSizes.size)
            return bm
        }

        private fun getRotation(context: Context, imageUri: Uri?): Int {
            val rotation: Int = getRotationFromCamera(context, imageUri)
            Log.d(TAG, "Image rotation: $rotation")
            return rotation
        }

        fun getRotationFromCamera(context: Context, imageFile: Uri?): Int {
            var rotate = 0
            try {
                context.contentResolver.notifyChange((imageFile)!!, null)
                val exif = ExifInterface((imageFile.path)!!)
                val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return rotate
        }

        private fun rotate(bm: Bitmap?, rotation: Int): Bitmap? {
            if (rotation != 0) {
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat())
                return Bitmap.createBitmap((bm)!!, 0, 0, bm.width, bm.height, matrix, true)
            }
            return bm
        }

        private fun getTempFile(context: Context): File {
            val imageFile = File(context.externalCacheDir, TEMP_IMAGE_NAME)
            imageFile.parentFile.mkdirs()
            return imageFile
        }
    }
}