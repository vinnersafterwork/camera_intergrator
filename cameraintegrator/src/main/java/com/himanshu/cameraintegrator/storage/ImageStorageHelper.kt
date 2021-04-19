package com.himanshu.cameraintegrator.storage

import android.content.Context
import android.graphics.Bitmap
import com.himanshu.cameraintegrator.storage.StorageHelper.createExternalCacheFile
import com.himanshu.cameraintegrator.storage.StorageHelper.createExternalFile
import com.himanshu.cameraintegrator.storage.StorageHelper.createExternalPublicFile
import com.himanshu.cameraintegrator.storage.StorageHelper.createInternalCacheFile
import com.himanshu.cameraintegrator.storage.StorageHelper.createInternalFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ImageStorageHelper {
    /**
     * Saves #bitmapToSave to #fileFullPath provided
     *
     * @param destFile     path where image will be saved
     * @param bitmapToSave image which will be saved
     * @return if file got saved successfully or not
     */
    @JvmStatic
    fun saveTo(destFile: File?, bitmapToSave: Bitmap) {
        try {
            FileOutputStream(destFile).use { out ->
                bitmapToSave.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    out
                ) // bmp is your Bitmap instance
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createCacheImageFile(context: Context?): File? {
        val imageName = createRandomImageFileName()
        return createCacheImageFile(context, imageName)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createCacheImageFile(context: Context?, fileName: String?): File? {
        return createInternalCacheFile(context!!, fileName!!)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    @JvmStatic
    fun createCacheImageFile(context: Context?, directoryName: String?, fileName: String?): File? {
        return createInternalCacheFile(context!!, directoryName, fileName!!)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createInternalImageFile(context: Context?): File? {
        val imageName = createRandomImageFileName()
        return createInternalImageFile(context, imageName)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createInternalImageFile(context: Context?, imageName: String?): File? {
        return createInternalImageFile(context, null, imageName)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    @JvmStatic
    fun createInternalImageFile(context: Context?, directory: String?, imageName: String?): File? {
        return createInternalFile(context!!, directory, imageName!!)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createExternalCacheImageFile(context: Context?): File? {
        val imageName = createRandomImageFileName()
        return createExternalCacheImageFile(context, imageName)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createExternalCacheImageFile(context: Context?, imageName: String?): File? {
        return createExternalCacheFile(context!!, imageName!!)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createExternalCacheImageFile(
        context: Context?,
        directory: String?,
        imageName: String?
    ): File? {
        return createExternalCacheFile(context!!, directory, imageName!!)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createExternalImageFile(context: Context?): File? {
        val imageName = createRandomImageFileName()
        return createExternalImageFile(context, imageName)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    fun createExternalImageFile(context: Context?, imageName: String?): File? {
        return createExternalImageFile(context, null, imageName)
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    @JvmStatic
    fun createExternalImageFile(context: Context?, directory: String?, imageName: String?): File? {
        return createExternalFile(context!!, directory, imageName!!)
    }

    /**
     * Creates a random name for an Image File
     *
     * @return
     */
    fun createRandomImageFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "image_$timestamp.jpg"
    }

    /**
     * Creates An Image File in $directoryName with name #imageName
     *
     * @return
     */
    fun createExternalPublicImageFile(completeImagePath: String?): File {
        return createExternalPublicImageFile(null, completeImagePath, null)
    }

    /**
     * Creates An Image File in $directoryName with $imageName name and $fileFormat format
     *
     * @param directoryName
     * @return
     */
    fun createExternalPublicImageFile(
        directoryName: String?,
        imageName: String?,
        fileFormat: String?
    ): File {
        return createExternalPublicImageFile(directoryName, "$imageName.$fileFormat")
    }

    /**
     * Creates An Image File in $directoryName with $imageName name and $fileFormat format
     *
     * @param directoryName
     * @return
     */
    fun createExternalPublicImageFile(
        directoryName: String?,
        imageNameWithExtension: String?
    ): File {
        return createExternalPublicFile(directoryName, imageNameWithExtension)
    }
}