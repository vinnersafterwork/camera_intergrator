package com.himanshu.cameraintegrator.integrator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.himanshu.cameraintegrator.ImageCallback;
import com.himanshu.cameraintegrator.ImagesSizes;
import com.himanshu.cameraintegrator.RequestSource;
import com.himanshu.cameraintegrator.Result;
import com.himanshu.cameraintegrator.exceptions.RuntimePermissionNotGrantedException;
import com.himanshu.cameraintegrator.executors.AppExecutors;
import com.himanshu.cameraintegrator.storage.StorageMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;


/**
 * Base Integrator
 */
public abstract class Integrator {


    /**
     * {@link AppExecutors} for switching threads
     */
    protected AppExecutors taskExecutors;
    protected @NonNull
    StorageMode storageMode = StorageMode.INTERNAL_FILE_STORAGE;
    /**
     * Context of calling activity
     */
    @NonNull
    Context mContext;

    public Integrator(Context context) {
        this.mContext = context;
        taskExecutors = AppExecutors.getInstance();
    }

    /**
     * Sets Where the Image will be stored INTERNAL_FILE_STORAGE or EXTERNAL_STORAGE
     * default is {@link StorageMode#INTERNAL_FILE_STORAGE}
     *
     * @param storageMode it can be one of these
     *                    <ul>
     *                    <li>{@link StorageMode#INTERNAL_FILE_STORAGE}</li>
     *                    <li>{@link StorageMode#INTERNAL_CACHE_STORAGE}</li>
     *                    <li>{@link StorageMode#EXTERNAL_CACHE_STORAGE}</li>
     *                    <li>{@link StorageMode#EXTERNAL_PUBLIC_STORAGE}</li>
     *                    </ul>
     */
    public void setStorageMode(StorageMode storageMode) {
        this.storageMode = storageMode;
    }

    /**
     * Reads the Image File without loading it onto memory and returns
     * information about the image file
     *
     * @param mFile file to image
     * @return
     */
    protected BitmapFactory.Options getImageMetaData(File mFile) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);

        return options;
    }


    /**
     * Loads Image in #requiredImageSize using {@link Glide}
     *
     * @param file
     * @param imageMetaData
     * @param requiredImageSize
     * @return
     */
    protected @Nullable
    Bitmap getRequiredSizeImage(File file, BitmapFactory.Options imageMetaData, @ImagesSizes.ImageSize int requiredImageSize) {
        ImagesSizes requiredSize = calculateRequiredHeight(requiredImageSize, imageMetaData);
        try {

            return Glide.
                    with(mContext)
                    .asBitmap()
                    .load(file)
                    .submit(requiredSize.getWidth(), requiredSize.getHeight())
                    .get();


        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Calculates the size of final image size without distorting the aspect ratio of the image
     *
     * @param requiredWidth
     * @param imageMetaData
     * @return
     */
    protected ImagesSizes calculateRequiredHeight(@ImagesSizes.ImageSize int requiredWidth, BitmapFactory.Options imageMetaData) {
        ImagesSizes finalImageSize = null;

        //Variables required for computation
        float requiredToCurrentWidthRatio;
        int finalImageHeight;

        switch (requiredWidth) {
            case ImagesSizes.FULL_SIZE:

                finalImageSize = new ImagesSizes();
                finalImageSize.setHeight(imageMetaData.outHeight);
                finalImageSize.setWidth(imageMetaData.outWidth);
                break;

            case ImagesSizes.HALF_SIZE:

                finalImageSize = new ImagesSizes();
                finalImageSize.setHeight(imageMetaData.outHeight / 2);
                finalImageSize.setWidth(imageMetaData.outWidth / 2);
                break;

            case ImagesSizes.OPTIMUM_BIG:
            case ImagesSizes.OPTIMUM_SMALL:
            case ImagesSizes.THUMBNAIL:
            case ImagesSizes.THUMBNAIL_BIG:

                finalImageSize = new ImagesSizes();

                requiredToCurrentWidthRatio = (float) requiredWidth / imageMetaData.outWidth;
                finalImageHeight = (int) (requiredToCurrentWidthRatio * imageMetaData.outHeight);

                finalImageSize.setHeight(finalImageHeight);
                finalImageSize.setWidth(requiredWidth);

                break;
            default:
                //If given condition matches ImagesSizes.OPTIMUM_MEDIUM or doesn't match up any condition at all
                //then we will give ImagesSizes.OPTIMUM_MEDIUM back

                finalImageSize = new ImagesSizes();

                requiredToCurrentWidthRatio = (float) requiredWidth / imageMetaData.outWidth;
                finalImageHeight = (int) (requiredToCurrentWidthRatio * imageMetaData.outHeight);

                finalImageSize.setHeight(finalImageHeight);
                finalImageSize.setWidth(requiredWidth);

                break;

        }
        return finalImageSize;
    }

    /**
     * Checks The {@link Manifest.permission#READ_EXTERNAL_STORAGE} Permission
     *
     * @throws RuntimePermissionNotGrantedException runtime permission if {@link Manifest.permission#READ_EXTERNAL_STORAGE} is not granted
     */
    void checkForReadStoragePermissions() throws RuntimePermissionNotGrantedException {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimePermissionNotGrantedException("read storage permission not granted");
        }
    }

    /**
     * Checks The {@link Manifest.permission#WRITE_EXTERNAL_STORAGE} Permission
     *
     * @throws RuntimePermissionNotGrantedException runtime permission if {@link Manifest.permission#WRITE_EXTERNAL_STORAGE} is not granted
     */
    void checkForWriteStoragePermissions() throws RuntimePermissionNotGrantedException {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimePermissionNotGrantedException("read storage permission not granted");
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
    protected Bitmap getBitmapInRequiredSize(File mFile,
                                             final int requiredImageSize) throws FileNotFoundException {

        if (!mFile.exists())
            throw new FileNotFoundException("file not found" + mFile.getAbsolutePath());

        //Reading data About the File
        BitmapFactory.Options sourceImageData = getImageMetaData(mFile);

        //Bitmap Of Requried Size
        return getRequiredSizeImage(mFile, sourceImageData, requiredImageSize);
    }

    /**
     * Prepares {@link Result} for delivering to user
     *
     * @param mFile  file where image is saved
     * @param image  image file
     * @param source request source
     * @return
     */
    protected Result getResults(File mFile,
                                Bitmap image,
                                @RequestSource.RequestSourceOptions final int source) {

        Result imageResults = new Result();
        imageResults.setWidth(image.getWidth());
        imageResults.setHeight(image.getHeight());
        imageResults.setBitmap(image);
        imageResults.setImageName(mFile.getName());
        imageResults.setImagePath(mFile.getAbsolutePath());
        imageResults.setFileSizeInMb(mFile.length() / (1024 * 1024));
        return imageResults;
    }

    /**
     * to be defined by child classes which will parse results and return back to user
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @param resultCallback
     */
    public abstract void parseResults(int requestCode, int resultCode, Intent data, final ImageCallback resultCallback);

    /**
     * For Saving State in case activity/Fragment gets destroyed by Android
     *
     * @param outState
     */
    public abstract void saveState(Bundle outState);

    /**
     * For Restoring state in case of activity or fragment gets regenerated
     */
    public abstract void restoreState(Bundle savedInstanceState);

}
