package com.himanshu.cameraintegrator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHelper {


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


    public static File createImageFile(String directoryName) {

        // Create a unique file name for image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        return createImageFile(directoryName, imageFileName, "jpg");
    }

    public static File createImageFile(String directoryName, String imageName) {

        return createImageFile(directoryName, imageName, "jpg");
    }

    public static File createImageFile(String directoryName, @Nullable String imageName, @NonNull String fileFormat) {

        // Getting a reference to Target storage directory
        File storageDir = Environment.getExternalStoragePublicDirectory(directoryName);

        // Creating directory if not made already
        storageDir.mkdirs();

        File image = new File(storageDir, imageName + "." + fileFormat);
        return image;
    }


    public static Bitmap scaleImageAtOptimum(Bitmap imageToScale) {

        ByteArrayOutputStream bytearrayoutputstream;
        byte[] BYTE;

        final long targetArea = 1228800;
        final int targetHeightPortrait = 1280;
        final int targetHeightLandscape = 960;

        float targetWidthPortrait;
        float targetWidthLandscape;

        long currentArea = 0;
        float currentToTargetRatio;

        if (imageToScale == null) {
            return null;
        } else {
            bytearrayoutputstream = new ByteArrayOutputStream();
            currentArea = imageToScale.getWidth() * imageToScale.getHeight();

            if (currentArea < targetArea)
                return imageToScale;
            else {
                Bitmap tempBitmap;
                if (imageToScale.getHeight() > imageToScale.getWidth()) {
                    //Height is more than width hence picture was clicked in portrait mode
                    currentToTargetRatio = (float) imageToScale.getHeight() / targetHeightPortrait;
                    targetWidthPortrait = imageToScale.getWidth() / currentToTargetRatio;
                    tempBitmap = Bitmap.createScaledBitmap(imageToScale, (int) targetWidthPortrait, targetHeightPortrait, false);
                } else {
                    //Height is more than width hence picture was clicked in landscape mode
                    currentToTargetRatio = (float) imageToScale.getWidth() / targetHeightLandscape;
                    targetWidthLandscape = imageToScale.getHeight() / currentToTargetRatio;
                    tempBitmap = Bitmap.createScaledBitmap(imageToScale, targetHeightLandscape, (int) targetWidthLandscape, false);

                }

                tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytearrayoutputstream);

                BYTE = bytearrayoutputstream.toByteArray();

                return BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
            }

        }

    }

    public static String getRealPathFromURI(Context c, Uri contentURI) {
        Cursor cursor = c.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
    }
}

