package com.himanshu.cameraintegrator.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
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
        return StorageHelper.createCacheFile(context, fileName);
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
        return StorageHelper.createInternalFile(context, imageName);
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
     * Creates a random name for an Image File
     *
     * @return
     */
    private static String createRandomImageFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "IMAGE_" + timestamp + ".jpg";
    }


    /**
     * Creates An Image File in $directoryName with name #imageName
     *
     * @param directoryName
     * @return
     */
    public static File createImageFile(String directoryName, String imageName) {
        return createImageFile(directoryName, imageName, "jpg");
    }

    /**
     * Creates An Image File in $directoryName with $imageName name and $fileFormat format
     *
     * @param directoryName
     * @return
     */
    public static File createImageFile(String directoryName, @Nullable String imageName, @NonNull String fileFormat) {

        // Getting a reference to Target storage directory
        File storageDir = Environment.getExternalStoragePublicDirectory(directoryName);

        // Creating directory if not made already
        storageDir.mkdirs();

        File image = new File(storageDir, imageName + "." + fileFormat);
        return image;
    }

}

