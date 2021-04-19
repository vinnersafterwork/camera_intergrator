package com.himanshu.cameraintegrator.integrator

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.himanshu.cameraintegrator.ImageCallback
import com.himanshu.cameraintegrator.ImagesSizes.ImageSize
import com.himanshu.cameraintegrator.RequestSource
import com.himanshu.cameraintegrator.exceptions.RuntimePermissionNotGrantedException
import com.himanshu.cameraintegrator.storage.ImageStorageHelper.createCacheImageFile
import com.himanshu.cameraintegrator.storage.ImageStorageHelper.createExternalCacheImageFile
import com.himanshu.cameraintegrator.storage.ImageStorageHelper.createExternalImageFile
import com.himanshu.cameraintegrator.storage.ImageStorageHelper.createInternalImageFile
import com.himanshu.cameraintegrator.storage.ImageStorageHelper.saveTo
import com.himanshu.cameraintegrator.storage.StorageMode
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by Himanshu on 4/30/2018.
 */
class GalleryIntegrator : Integrator {
    /**
     * Activity Context
     */
    private lateinit var activityRef: Activity

    /**
     * Reference of calling Activity
     */
    private lateinit var fragmentReference: Fragment

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

    /**
     * Required Size of the image
     * this can be one of the [ImagesSizes]
     */
    private var requiredImageSize = 0

    /**
     * @param activityRef path where the new image we clicked should be stored
     */
    constructor(activityRef: Activity) : super(activityRef) {
        this.activityRef = activityRef
    }

    constructor(fragmentReference: Fragment) : super(fragmentReference.requireActivity()) {
        this.fragmentReference = fragmentReference
        activityRef = fragmentReference.requireActivity()
    }

    fun setRequiredImageSize(@ImageSize requiredImageSize: Int) {
        this.requiredImageSize = requiredImageSize
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

    fun setImageDirectoryName(directoryName: String?) {
        imageDirectoryName = directoryName
    }

    /**
     * Initiates Image Pick Process
     *
     * @throws ActivityNotFoundException if There is no application to pick image
     */
    @Throws(ActivityNotFoundException::class, RuntimePermissionNotGrantedException::class)
    fun initiateImagePick() {
        checkForReadStoragePermissions()

        //Checking Write Permission If Output File is Supposed to be stored in the External Storage
        if (storageMode == StorageMode.EXTERNAL_CACHE_STORAGE || storageMode == StorageMode.EXTERNAL_PUBLIC_STORAGE)
            checkForWriteStoragePermissions()

        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

        if (::fragmentReference.isInitialized.not()) {
            activityRef.startActivityForResult(
                intent,
                REQUEST_IMAGE_PICK
            )
        } else {
            fragmentReference.startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun parseResults(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        resultCallback: ImageCallback
    ) {
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data!!.data
            if (selectedImageUri != null) {
                val imagePath = getRealPathFromURI(activityRef, selectedImageUri)

                taskExecutors.diskIO().execute(Runnable { //Reference to file picked By User
                    val mFile = File(imagePath)
                    var destFile: File? = null
                    when (storageMode) {
                        StorageMode.INTERNAL_FILE_STORAGE ->
                            //Creating A File In Internal Storage which app will use
                            destFile = if (imageDirectoryName != null) createInternalImageFile(
                                mContext,
                                imageDirectoryName,
                                mFile.name
                            ) else createInternalImageFile(mContext, mFile.name)
                        StorageMode.INTERNAL_CACHE_STORAGE ->
                            //Creating A File In Internal Storage which app will use
                            destFile = if (imageDirectoryName != null) createCacheImageFile(
                                mContext,
                                imageDirectoryName,
                                mFile.name
                            ) else createCacheImageFile(mContext, mFile.name)
                        StorageMode.EXTERNAL_CACHE_STORAGE ->
                            //Creating A File In Internal Storage which app will use
                            destFile =
                                if (imageDirectoryName != null) createExternalCacheImageFile(
                                    mContext,
                                    imageDirectoryName + "/" + mFile.name
                                ) else createExternalCacheImageFile(mContext, mFile.name)
                        StorageMode.EXTERNAL_FILE_STORAGE ->
                            //Creating A File In Internal Storage which app will use
                            destFile = if (imageDirectoryName != null) createExternalImageFile(
                                mContext,
                                imageDirectoryName,
                                mFile.name
                            ) else createExternalImageFile(mContext, mFile.name)
                        StorageMode.EXTERNAL_PUBLIC_STORAGE -> {
                            val storageDir: File

                            // Getting a reference to Target storage directory
                            storageDir = if (publicDirectoryName != null) {
                                if (imageDirectoryName != null) Environment.getExternalStoragePublicDirectory(
                                    "$publicDirectoryName/$imageDirectoryName"
                                ) else Environment.getExternalStoragePublicDirectory(
                                    publicDirectoryName
                                )
                            } else Environment.getExternalStoragePublicDirectory(
                                imageDirectoryName
                            )

                            // Creating directory if not made already
                            if (!storageDir.exists()) storageDir.mkdirs()
                            destFile = File(storageDir, mFile.name)
                        }
                    }


                    //Getting Bitmap Of Required Size
                    val requiredSizeImage: Bitmap?
                    requiredSizeImage = try {
                        getBitmapInRequiredSize(mFile, requiredImageSize)
                    } catch (e: FileNotFoundException) {
                        resultCallback!!.onResult(RequestSource.SOURCE_GALLERY, null, e)
                        return@Runnable
                    }

                    // Saving the Bitmap of required size to the required directory
                    saveTo(destFile, requiredSizeImage!!)

                    //Preparing Results object
                    val results =
                        getResults(destFile!!, requiredSizeImage, RequestSource.SOURCE_GALLERY)

                    //Delivering Results back to main thread
                    taskExecutors.mainThread().execute {
                        resultCallback!!.onResult(
                            RequestSource.SOURCE_GALLERY,
                            results,
                            null
                        )
                    }
                })

            }
        }
    }

    override fun saveState(outState: Bundle) {
        outState!!.putString(INTENT_EXTRA_FILE_DIRECTORY_NAME, imageDirectoryName)
        outState.putInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, requiredImageSize)
    }

    override fun restoreState(savedInstanceState: Bundle) {
        imageDirectoryName = savedInstanceState!!.getString(INTENT_EXTRA_FILE_DIRECTORY_NAME, null)
        requiredImageSize = savedInstanceState.getInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, -1)
    }

    /**
     * Parses #contentURI and returns Absolute path of image
     *
     * @param c
     * @param contentURI
     * @return
     */
    private fun getRealPathFromURI(c: Context, contentURI: Uri): String {
        val cursor = c.contentResolver.query(contentURI, null, null, null, null)
        return if (cursor == null) {
            // path
            contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val path = cursor.getString(idx)
            cursor.close()
            path
        }
    }

    companion object {
        /**
         * Constant to be used for identifying image pick action
         */
        const val REQUEST_IMAGE_PICK = 2
        private const val INTENT_EXTRA_FILE_DIRECTORY_NAME = "gallery_image_directory_name"
        private const val INTENT_EXTRA_FINAL_REQUIRED_SIZE = "gallery_required_size"
    }
}