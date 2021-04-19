package com.himanshu.cameraintegrator.integrator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.himanshu.cameraintegrator.ImageCallback
import com.himanshu.cameraintegrator.ImagesSizes
import com.himanshu.cameraintegrator.ImagesSizes.ImageSize
import com.himanshu.cameraintegrator.RequestSource.RequestSourceOptions
import com.himanshu.cameraintegrator.Result
import com.himanshu.cameraintegrator.exceptions.RuntimePermissionNotGrantedException
import com.himanshu.cameraintegrator.executors.AppExecutors
import com.himanshu.cameraintegrator.storage.StorageMode
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.ExecutionException

/**
 * Base Integrator
 */
abstract class Integrator(
    /**
     * Context of calling activity
     */
    var mContext: Context
) {
    /**
     * [AppExecutors] for switching threads
     */

    protected var taskExecutors: AppExecutors = AppExecutors.instance


    var storageMode = StorageMode.INTERNAL_FILE_STORAGE

    /**
     * Sets Where the Image will be stored INTERNAL_FILE_STORAGE or EXTERNAL_STORAGE
     * default is [StorageMode.INTERNAL_FILE_STORAGE]
     *
     * @param storageMode it can be one of these
     *
     *  * [StorageMode.INTERNAL_FILE_STORAGE]
     *  * [StorageMode.INTERNAL_CACHE_STORAGE]
     *  * [StorageMode.EXTERNAL_CACHE_STORAGE]
     *  * [StorageMode.EXTERNAL_PUBLIC_STORAGE]
     *
     */


    /**
     * Reads the Image File without loading it onto memory and returns
     * information about the image file
     *
     * @param mFile file to image
     * @return
     */
    protected fun getImageMetaData(mFile: File): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mFile.absolutePath, options)
        return options
    }

    /**
     * Loads Image in #requiredImageSize using [Glide]
     *
     * @param file
     * @param imageMetaData
     * @param requiredImageSize
     * @return
     */
    protected fun getRequiredSizeImage(
        file: File?,
        imageMetaData: BitmapFactory.Options,
        @ImageSize requiredImageSize: Int
    ): Bitmap? {
        val requiredSize = calculateRequiredHeight(requiredImageSize, imageMetaData)
        return try {
            Glide.with(mContext)
                .asBitmap()
                .load(file)
                .submit(requiredSize.width, requiredSize.height)
                .get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calculates the size of final image size without distorting the aspect ratio of the image
     *
     * @param requiredWidth
     * @param imageMetaData
     * @return
     */
    protected fun calculateRequiredHeight(
        @ImageSize requiredWidth: Int,
        imageMetaData: BitmapFactory.Options
    ): ImagesSizes {
        var finalImageSize: ImagesSizes? = null

        //Variables required for computation
        val requiredToCurrentWidthRatio: Float
        val finalImageHeight: Int
        when (requiredWidth) {
            ImagesSizes.FULL_SIZE -> {
                finalImageSize = ImagesSizes()
                finalImageSize.height = imageMetaData.outHeight
                finalImageSize.width = imageMetaData.outWidth
            }
            ImagesSizes.HALF_SIZE -> {
                finalImageSize = ImagesSizes()
                finalImageSize.height = imageMetaData.outHeight / 2
                finalImageSize.width = imageMetaData.outWidth / 2
            }
            ImagesSizes.OPTIMUM_BIG, ImagesSizes.OPTIMUM_SMALL, ImagesSizes.THUMBNAIL, ImagesSizes.THUMBNAIL_BIG -> {
                finalImageSize = ImagesSizes()
                requiredToCurrentWidthRatio = requiredWidth.toFloat() / imageMetaData.outWidth
                finalImageHeight = (requiredToCurrentWidthRatio * imageMetaData.outHeight).toInt()
                finalImageSize.height = finalImageHeight
                finalImageSize.width = requiredWidth
            }
            else -> {
                //If given condition matches ImagesSizes.OPTIMUM_MEDIUM or doesn't match up any condition at all
                //then we will give ImagesSizes.OPTIMUM_MEDIUM back
                finalImageSize = ImagesSizes()
                requiredToCurrentWidthRatio = requiredWidth.toFloat() / imageMetaData.outWidth
                finalImageHeight = (requiredToCurrentWidthRatio * imageMetaData.outHeight).toInt()
                finalImageSize.height = finalImageHeight
                finalImageSize.width = requiredWidth
            }
        }
        return finalImageSize
    }

    /**
     * Checks The [Manifest.permission.READ_EXTERNAL_STORAGE] Permission
     *
     * @throws RuntimePermissionNotGrantedException runtime permission if [Manifest.permission.READ_EXTERNAL_STORAGE] is not granted
     */
    @Throws(RuntimePermissionNotGrantedException::class)
    fun checkForReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw RuntimePermissionNotGrantedException("read storage permission not granted")
        }
    }

    /**
     * Checks The [Manifest.permission.WRITE_EXTERNAL_STORAGE] Permission
     *
     * @throws RuntimePermissionNotGrantedException runtime permission if [Manifest.permission.WRITE_EXTERNAL_STORAGE] is not granted
     */
    @Throws(RuntimePermissionNotGrantedException::class)
    fun checkForWriteStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw RuntimePermissionNotGrantedException("read storage permission not granted")
        }
    }

    /**
     * Reads Bitmap from storage
     *
     * @param mFile             file to read
     * @param requiredImageSize size of required image
     * @return image in required size
     * @throws FileNotFoundException file not found
     */
    @Throws(FileNotFoundException::class)
    protected fun getBitmapInRequiredSize(
        mFile: File,
        requiredImageSize: Int
    ): Bitmap? {
        if (!mFile.exists()) throw FileNotFoundException("file not found" + mFile.absolutePath)

        //Reading data About the File
        val sourceImageData = getImageMetaData(mFile)

        //Bitmap Of Requried Size
        return getRequiredSizeImage(mFile, sourceImageData, requiredImageSize)
    }

    /**
     * Prepares [Result] for delivering to user
     *
     * @param mFile  file where image is saved
     * @param image  image file
     * @param source request source
     * @return
     */
    protected fun getResults(
        mFile: File,
        image: Bitmap,
        @RequestSourceOptions source: Int
    ): Result {
        val imageResults = Result()
        imageResults.width = image.width
        imageResults.height = image.height
        imageResults.bitmap = image
        imageResults.imageName = mFile.name
        imageResults.imagePath = mFile.absolutePath
        imageResults.fileSizeInMb = mFile.length() / (1024 * 1024)
        return imageResults
    }

    /**
     * to be defined by child classes which will parse results and return back to user
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @param resultCallback
     */
    abstract fun parseResults(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        resultCallback: ImageCallback
    )

    /**
     * For Saving State in case activity/Fragment gets destroyed by Android
     *
     * @param outState
     */
    abstract fun saveState(outState: Bundle)

    /**
     * For Restoring state in case of activity or fragment gets regenerated
     */
    abstract fun restoreState(savedInstanceState: Bundle)

}