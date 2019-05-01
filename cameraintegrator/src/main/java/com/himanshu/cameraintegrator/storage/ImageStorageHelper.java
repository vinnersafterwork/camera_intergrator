package com.himanshu.cameraintegrator.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageStorageHelper {


    /**
     * Saves #bitmapToSave to #fileFullPath provided
     *
     * @param destFile     path where image will be saved
     * @param bitmapToSave image which will be saved
     * @return if file got saved successfully or not
     */
    public static void saveTo(File destFile, Bitmap bitmapToSave) {

        try (FileOutputStream out = new FileOutputStream(destFile)) {
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createCacheImageFile(Context context) {
        String imageName = createRandomImageFileName();
        return createCacheImageFile(context, imageName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createCacheImageFile(Context context, String fileName) {
        return StorageHelper.createInternalCacheFile(context, fileName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createCacheImageFile(Context context, String directoryName, String fileName) {
        return StorageHelper.createInternalCacheFile(context, directoryName, fileName);
    }


    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createInternalImageFile(Context context) {
        String imageName = createRandomImageFileName();
        return createInternalImageFile(context, imageName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createInternalImageFile(Context context, String imageName) {
        return createInternalImageFile(context, null, imageName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createInternalImageFile(Context context, String directory, String imageName) {
        return StorageHelper.createInternalFile(context, directory, imageName);
    }


    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createExternalCacheImageFile(Context context) {
        String imageName = createRandomImageFileName();
        return createExternalCacheImageFile(context, imageName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createExternalCacheImageFile(Context context, String imageName) {
        return StorageHelper.createExternalCacheFile(context, imageName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createExternalCacheImageFile(Context context, String directory, String imageName) {
        return StorageHelper.createExternalCacheFile(context, directory, imageName);
    }


    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createExternalImageFile(Context context) {
        String imageName = createRandomImageFileName();
        return createExternalImageFile(context, imageName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createExternalImageFile(Context context, String imageName) {
        return createExternalImageFile(context, null, imageName);
    }

    /**
     * Creates An Image File in $directoryName
     *
     * @return
     */
    public static File createExternalImageFile(Context context, String directory, String imageName) {
        return StorageHelper.createExternalFile(context, directory, imageName);
    }


    /**
     * Creates a random name for an Image File
     *
     * @return
     */
    public static String createRandomImageFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "image_" + timestamp + ".jpg";
    }


    /**
     * Creates An Image File in $directoryName with name #imageName
     *
     * @return
     */
    public static File createExternalPublicImageFile(String completeImagePath) {
        return createExternalPublicImageFile(null, completeImagePath, null);
    }

    /**
     * Creates An Image File in $directoryName with $imageName name and $fileFormat format
     *
     * @param directoryName
     * @return
     */
    public static File createExternalPublicImageFile(String directoryName, @Nullable String imageName, @Nullable String fileFormat) {
        return createExternalPublicImageFile(directoryName, imageName + "." + fileFormat);
    }

    /**
     * Creates An Image File in $directoryName with $imageName name and $fileFormat format
     *
     * @param directoryName
     * @return
     */
    public static File createExternalPublicImageFile(String directoryName, @Nullable String imageNameWithExtension) {
        return StorageHelper.createExternalPublicFile(directoryName, imageNameWithExtension);
    }

}

