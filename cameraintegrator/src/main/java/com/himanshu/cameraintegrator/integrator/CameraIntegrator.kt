package com.himanshu.cameraintegrator.integrator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import com.himanshu.cameraintegrator.ImageCallback
import com.himanshu.cameraintegrator.ImagesSizes.ImageSize
import com.himanshu.cameraintegrator.RequestSource
import com.himanshu.cameraintegrator.exceptions.CameraActivityNotFoundException
import com.himanshu.cameraintegrator.exceptions.RuntimePermissionNotGrantedException
import com.himanshu.cameraintegrator.storage.ImageStorageHelper
import com.himanshu.cameraintegrator.storage.StorageMode
import java.io.File
import java.io.FileNotFoundException
import java.lang.IllegalStateException

/**
 * Camera Intent Integrator simply removes the boiler plate code that need to generate
 * while using native camera app to click images
 *
 * @author Himanshu
 */
class CameraIntegrator : Integrator {
    /**
     * Activity Context
     */
    private val activityReference: Activity?
    private var fragmentReference: Fragment? = null

    /**
     * File instance on which we will be operating
     */
    private var mFile: File? = null

    /**
     * This Value can be given
     */
    private var imagePath: String? = null

    /**
     * Public Directory Name it can be
     *
     *  * [Environment.DIRECTORY_MUSIC]
     *  * [Environment.DIRECTORY_PODCASTS]
     *  * [Environment.DIRECTORY_RINGTONES]
     *  * [Environment.DIRECTORY_ALARMS]
     *  * [Environment.DIRECTORY_NOTIFICATIONS]
     *  * [Environment.DIRECTORY_PICTURES]     *
     *  * [Environment.DIRECTORY_MOVIES]
     *  * [Environment.DIRECTORY_DOWNLOADS]
     *  * [Environment.DIRECTORY_DCIM]
     *  * [Environment.DIRECTORY_DOCUMENTS]     *
     *
     */
    private var publicDirectoryName: String? = null

    /**
     * Directory Name Where Image will be kept
     */
    private var imageDirectoryName: String? = null
    private var imageName: String? = null
    private var imageFormat: String? = null

    /**
     * Required Size of the image
     * this can be one of the [ImagesSizes]
     */
    private var requiredImageSize = 0

    /**
     * @param activityReference path where the new image we clicked should be stored
     */
    constructor(activityReference: Activity) : super(activityReference) {
        this.activityReference = activityReference
    }

    constructor(fragmentReference: Fragment) : super(fragmentReference.requireContext()) {
        this.fragmentReference = fragmentReference
        activityReference = fragmentReference.activity
    }

    /**
     * Sets Public Directory Name
     *
     * @param publicDirectoryName it can be
     *
     *  * [Environment.DIRECTORY_MUSIC]
     *  * [Environment.DIRECTORY_PODCASTS]
     *  * [Environment.DIRECTORY_RINGTONES]
     *  * [Environment.DIRECTORY_ALARMS]
     *  * [Environment.DIRECTORY_NOTIFICATIONS]
     *  * [Environment.DIRECTORY_PICTURES]
     *  * [Environment.DIRECTORY_MOVIES]
     *  * [Environment.DIRECTORY_DOWNLOADS]
     *  * [Environment.DIRECTORY_DCIM]
     *  * [Environment.DIRECTORY_DOCUMENTS]
     *
     */
    fun setPublicDirectoryName(publicDirectoryName: String?) {
        this.publicDirectoryName = publicDirectoryName
    }

    /**
     * Setting the absolute path where image will be stored
     *
     * @param imagePath
     */
    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    fun setImageDirectoryName(directoryName: String?) {
        imageDirectoryName = directoryName
    }

    fun setImageName(imageName: String?) {
        this.imageName = imageName
    }

    fun setRequiredImageSize(@ImageSize requiredImageSize: Int) {
        this.requiredImageSize = requiredImageSize
    }

    /**
     * Opens Camera for capturing image
     */
    @Throws(CameraActivityNotFoundException::class, RuntimePermissionNotGrantedException::class)
    fun initiateCapture() {
        if (storageMode == StorageMode.EXTERNAL_PUBLIC_STORAGE || storageMode == StorageMode.EXTERNAL_CACHE_STORAGE) {
            checkForWriteStoragePermissions()
        }

        //Lets create a file if user hasn't created one where image will be stored
        mFile = if (imagePath != null) File(imagePath) else createDestinationImageFile()

        if (mFile == null) throw RuntimeException("Details Where image should be stored are not provided, you can call ")
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(activityReference!!.packageManager) != null) {

            // Continue only if the File was successfully created
            if (mFile != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile))
                } else {
                    val file = File(Uri.fromFile(mFile).path)
                    val photoUri = FileProvider.getUriForFile(
                        activityReference.applicationContext,
                        activityReference.packageName + ".provider",
                        file
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                }
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (fragmentReference == null) activityReference.startActivityForResult(
                    cameraIntent,
                    REQUEST_IMAGE_CAPTURE
                ) else fragmentReference!!.startActivityForResult(
                    cameraIntent,
                    REQUEST_IMAGE_CAPTURE
                )
            }
        } else throw CameraActivityNotFoundException("No Camera App found to initiate capture")
    }

    /**
     * Creates Destination Image File which will be passed to camera App, where Camera App will store the image
     *
     * @return
     */
    private fun createDestinationImageFile(): File? {
        val imageDirectoryFinalPath = StringBuilder()
        val imageFinalName: String
        when (storageMode) {
            StorageMode.EXTERNAL_PUBLIC_STORAGE -> {
                if (publicDirectoryName != null) imageDirectoryFinalPath.append(publicDirectoryName)
                if (imageDirectoryFinalPath.length != 0) imageDirectoryFinalPath.append("/")
                if (imageDirectoryName != null) imageDirectoryFinalPath.append(imageDirectoryName)
            }
            StorageMode.INTERNAL_CACHE_STORAGE, StorageMode.INTERNAL_FILE_STORAGE, StorageMode.EXTERNAL_CACHE_STORAGE -> {
                if (imageDirectoryFinalPath.length != 0) imageDirectoryFinalPath.append("/")
                if (imageDirectoryName != null) imageDirectoryFinalPath.append(imageDirectoryName)
            }
        }
        imageFinalName = if (imageName != null) {
            if (imageName!!.endsWith(".jpg")) imageName!! else "$imageName.jpg"
        } else ImageStorageHelper.createRandomImageFileName()
        var imageFile: File? = null
        imageFile = when (storageMode) {
            StorageMode.INTERNAL_CACHE_STORAGE -> ImageStorageHelper.createCacheImageFile(
                mContext,
                imageDirectoryFinalPath.toString(),
                imageFinalName
            )
            StorageMode.INTERNAL_FILE_STORAGE -> ImageStorageHelper.createInternalImageFile(
                mContext,
                imageDirectoryFinalPath.toString(),
                imageFinalName
            )
            StorageMode.EXTERNAL_CACHE_STORAGE -> ImageStorageHelper.createExternalCacheImageFile(
                mContext,
                imageDirectoryFinalPath.toString(),
                imageFinalName
            )
            StorageMode.EXTERNAL_FILE_STORAGE -> ImageStorageHelper.createExternalImageFile(
                mContext,
                imageDirectoryFinalPath.toString(),
                imageFinalName
            )
            StorageMode.EXTERNAL_PUBLIC_STORAGE -> ImageStorageHelper.createExternalPublicImageFile(
                imageDirectoryFinalPath.toString(),
                imageFinalName
            )
        }
        return imageFile
    }


    override fun parseResults(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        resultCallback: ImageCallback
    ) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            if (mFile == null) return

            taskExecutors.diskIO().execute {
                try {
                    val requiredSizeImage = getBitmapInRequiredSize(mFile!!, requiredImageSize)
                        ?: throw IllegalStateException("Unable to generate required file size")

                    //Now replacing mFile
                    // Saving the Bitmap of required size to the required directory
                    ImageStorageHelper.saveTo(mFile, requiredSizeImage)

                    //Preparing Results object
                    val results =
                        getResults(mFile!!, requiredSizeImage, RequestSource.SOURCE_CAMERA)

                    //Delivering Results back to main thread
                    taskExecutors.mainThread().execute {
                        resultCallback.onResult(RequestSource.SOURCE_CAMERA, results, null)
                        removeUsedFields()
                    }
                } catch (e: FileNotFoundException) {
                    taskExecutors.mainThread().execute {
                        resultCallback.onResult(RequestSource.SOURCE_CAMERA, null, e)
                        removeUsedFields()
                    }
                }
            }
        }
    }

    private fun removeUsedFields() {
        imagePath = null
        mFile = null
    }

    override fun saveState(outState: Bundle) {
        if (mFile != null) outState.putString(INTENT_EXTRA_FILE_COMPLETE_PATH, mFile!!.absolutePath)
        outState.putString(INTENT_EXTRA_FILE_DIRECTORY_NAME, imageDirectoryName)
        outState.putString(INTENT_EXTRA_FILE_IMAGE_FORMAT, imageFormat)
        outState.putString(INTENT_EXTRA_FILE_IMAGE_NAME, imageName)
        outState.putInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, requiredImageSize)
        outState.putString(INTENT_EXTRA_STORAGE_MODE, storageMode.name)
        outState.putString(INTENT_EXTRA_FILE_PUBLIC_DIRECTORY_NAME, publicDirectoryName)
    }

    override fun restoreState(savedInstanceState: Bundle) {
        imagePath = savedInstanceState.getString(INTENT_EXTRA_FILE_COMPLETE_PATH, null)
        imageDirectoryName = savedInstanceState.getString(INTENT_EXTRA_FILE_DIRECTORY_NAME, null)
        imageFormat = savedInstanceState.getString(INTENT_EXTRA_FILE_IMAGE_FORMAT, null)
        imageName = savedInstanceState.getString(INTENT_EXTRA_FILE_IMAGE_NAME, null)
        requiredImageSize = savedInstanceState.getInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, -1)
        publicDirectoryName = savedInstanceState.getString(INTENT_EXTRA_FILE_PUBLIC_DIRECTORY_NAME)
        val storageModeString = savedInstanceState.getString(INTENT_EXTRA_STORAGE_MODE)
        if (storageModeString != null) storageMode = StorageMode.valueOf(storageModeString)
        if (imagePath != null) mFile = File(imagePath)
    }

    companion object {
        /**
         * Constant to be used for opening Image
         */
        const val REQUEST_IMAGE_CAPTURE = 21
        private const val INTENT_EXTRA_FILE_COMPLETE_PATH = "complete_path"
        private const val INTENT_EXTRA_FILE_DIRECTORY_NAME = "camera_image_directory_name"
        private const val INTENT_EXTRA_FILE_PUBLIC_DIRECTORY_NAME = "public_directory"
        private const val INTENT_EXTRA_FILE_IMAGE_NAME = "image_name"
        private const val INTENT_EXTRA_FILE_IMAGE_FORMAT = "format"
        private const val INTENT_EXTRA_FINAL_REQUIRED_SIZE = "required_size"
        private const val INTENT_EXTRA_STORAGE_MODE = "storage_mode"
    }
}