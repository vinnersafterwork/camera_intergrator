package com.himanshu.cameraintegrator;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHelper {


    /**
     * Saves #bitmapToSave to #fileFullPath provided
     *
     * @param fileFullPath path where image will be saved
     * @param bitmapToSave image which will be saved
     * @return if file got saved successfully or not
     */
    public static boolean saveTo(String fileFullPath, Bitmap bitmapToSave) {

        FileOutputStream out = null;
        File file = new File(fileFullPath);
        try {
            out = new FileOutputStream(file);
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
        return false;
    }


    /**
     * Creates An Image File in $directoryName
     *
     * @param directoryName
     * @return
     */
    public static File createImageFile(String directoryName) {

        // Create a unique file name for image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        return createImageFile(directoryName, imageFileName, "jpg");
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

