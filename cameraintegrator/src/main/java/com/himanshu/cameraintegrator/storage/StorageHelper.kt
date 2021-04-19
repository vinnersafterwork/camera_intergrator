package com.himanshu.cameraintegrator.storage

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

object StorageHelper {
    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data,which is accessible to app only
     * and it gets deleted when user clears app data, or uninstalls the app,Android can delete it when storage is low
     *
     * @param context               context
     * @param fileNameWithExtension file name with extension (ex .jpg)
     * @return cache file
     */
    fun createInternalCacheFile(context: Context, fileNameWithExtension: String): File? {
        return createInternalCacheFile(context, null, fileNameWithExtension)
    }

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data,which is accessible to app only
     * and it gets deleted when user clears app data, or uninstalls the app,Android can delete it when storage is low
     *
     * @param context   context
     * @param fileName  filename without extension (ex .jpg)
     * @param extension extension
     * @return cache file
     */
    fun createInternalCacheFile(
        context: Context,
        directory: String?,
        fileName: String,
        extension: String
    ): File? {
        val finalFileName = fileName + if (extension.startsWith(".")) extension else ".$extension"
        return createInternalCacheFile(context, directory, finalFileName)
    }

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data, which is accessible to app only
     * and it gets deleted when user clears app data or uninstalls the app,Android can delete it when storage is low
     *
     * @param context               context
     * @param fileNameWithExtension file name with extension
     * @return cache file
     */
    @JvmStatic
    fun createInternalCacheFile(
        context: Context,
        directory: String?,
        fileNameWithExtension: String
    ): File? {
        val parentDirectory: File
        parentDirectory =
            if (directory != null) File(context.cacheDir, directory) else context.filesDir
        if (!parentDirectory.exists()) parentDirectory.mkdirs()
        return File(parentDirectory, fileNameWithExtension)
    }

    /**
     * Creates A Cache File In Eternal Storage ,files present in this directory is visib;e to other apps
     * and it gets deleted when user clears app data, or uninstalls the app,Android can delete it when storage is low
     *
     * @param context   context
     * @param fileName  filename without extension (ex .jpg)
     * @param extension extension
     * @return
     */
    fun createExternalCacheFile(
        context: Context,
        directory: String?,
        fileName: String,
        extension: String
    ): File? {
        val finalFileName = fileName + if (extension.startsWith(".")) extension else ".$extension"
        return createExternalCacheFile(context, directory, finalFileName)
    }

    /**
     * Creates A Cache File In Eternal Storage ,files present in this directory is visib;e to other apps
     * and it gets deleted when user clears app data or uninstalls the app,Android can delete it when storage is low
     *
     * @param context               context
     * @param fileNameWithExtension file name with extension
     * @return
     */
    fun createExternalCacheFile(context: Context, fileNameWithExtension: String): File? {
        return createExternalCacheFile(context, null, fileNameWithExtension)
    }

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data
     * and it gets deleted when user clears app data, or uninstalls app
     *
     * @param context  context
     * @param fileName fileName with extension
     * @return file
     */
    @JvmStatic
    fun createExternalCacheFile(context: Context, directory: String?, fileName: String): File? {
        val parentDirectory: File
        parentDirectory = if (directory != null) File(
            context.externalCacheDir,
            directory
        ) else context.filesDir
        if (!parentDirectory.exists()) parentDirectory.mkdirs()
        return File(parentDirectory, fileName)
    }

    /**
     * Creates A File In the Internal Storage which is accessible to app only
     * and it gets deleted when user clears app data or uninstalls app
     *
     * @param context               context
     * @param fileNameWithExtension filename with extension (ex .jpg)
     * @return
     */
    fun createInternalFile(context: Context, fileNameWithExtension: String): File? {
        return createInternalFile(context, null, fileNameWithExtension)
    }

    /**
     * Creates A File In the Internal Storage which is accessible to app only
     * and it gets deleted when user clears app data or uninstalls app
     *
     * @param context   context
     * @param fileName  filename without extension (ex .jpg)
     * @param extension extension
     * @return
     */
    fun createInternalFile(
        context: Context,
        directory: String?,
        fileName: String,
        extension: String
    ): File? {
        val finalFileName = fileName + if (extension.startsWith(".")) extension else ".$extension"
        return createInternalFile(context, directory, finalFileName)
    }

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data
     * and it gets deleted when user clears app data, or uninstalls app
     *
     * @param context  context
     * @param fileName fileName with extension
     * @return file
     */
    @JvmStatic
    fun createInternalFile(context: Context, directory: String?, fileName: String): File? {
        val parentDirectory: File
        parentDirectory =
            if (directory != null) File(context.filesDir, directory) else context.filesDir
        if (!parentDirectory.exists()) parentDirectory.mkdirs()
        return File(parentDirectory, fileName)
    }

    /**
     * Creates A Cache File In Eternal Storage ,files present in this directory is visib;e to other apps
     * and it gets deleted when user clears app data or uninstalls the app,Android can delete it when storage is low
     *
     * @param context               context
     * @param fileNameWithExtension fileName with extension
     * @return
     */
    fun createExternalFile(context: Context, fileNameWithExtension: String): File? {
        return createExternalFile(context, null, fileNameWithExtension)
    }

    /**
     * Creates A Cache File In Eternal Storage ,files present in this directory is visib;e to other apps
     * and it gets deleted when user clears app data, or uninstalls the app,Android can delete it when storage is low
     *
     * @param context   context
     * @param fileName  filename without extension (ex .jpg)
     * @param extension extension
     * @return
     */
    fun createExternalFile(
        context: Context,
        directory: String?,
        fileName: String,
        extension: String
    ): File? {
        val finalFileName = fileName + if (extension.startsWith(".")) extension else ".$extension"
        return createExternalFile(context, directory, finalFileName)
    }

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data
     * and it gets deleted when user clears app data, or uninstalls app
     *
     * @param context  context
     * @param fileName fileName with extension
     * @return file
     */
    @JvmStatic
    fun createExternalFile(context: Context, directory: String?, fileName: String): File? {
        val parentDirectory: File
        parentDirectory = if (directory != null) File(
            context.getExternalFilesDir(null),
            directory
        ) else context.getExternalFilesDir(null)
        if (!parentDirectory.exists()) parentDirectory.mkdirs()
        return File(parentDirectory, fileName)
    }

    /**
     * Creates a file in public external directory, which is accessible to others apps too
     * this file will be retained even after app is uninstalled
     *
     * @param directoryName directory name
     * @param imageName     image name
     * @param extension     extensions
     * @return
     */
    fun createExternalPublicFile(
        directoryName: String?,
        imageName: String,
        extension: String
    ): File {
        val finalFileName = imageName + if (extension.startsWith(".")) extension else ".$extension"
        return createExternalPublicFile(directoryName, finalFileName)
    }

    /**
     * Creates a file in public external directory, which is accessible to others apps too
     * this file will be retained even after app is uninstalled
     *
     * @param directoryName directory name
     * @param imageName     image name with extension
     * @return ref to file
     */
    @JvmStatic
    fun createExternalPublicFile(directoryName: String?, imageName: String?): File {

        // Getting a reference to Target storage directory
        val storageDir = Environment.getExternalStoragePublicDirectory(directoryName)
        if (!storageDir.exists()) storageDir.mkdirs()
        return File(storageDir, imageName)
    }

    /**
     * Copies Content Of #source to #destination
     *
     * @param source      source file
     * @param destination dest file
     */
    fun copy(source: File?, destination: File?) {
        var src: FileChannel? = null
        var dest: FileChannel? = null
        try {
            src = FileInputStream(source).channel
            dest = FileOutputStream(destination).channel
            dest.transferFrom(src, 0, src.size())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                src?.close()
                dest?.close()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}