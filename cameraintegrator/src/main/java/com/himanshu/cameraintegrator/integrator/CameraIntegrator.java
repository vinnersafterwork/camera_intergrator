package com.himanshu.cameraintegrator.integrator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import com.himanshu.cameraintegrator.*;
import com.himanshu.cameraintegrator.exceptions.CameraActivityNotFoundException;
import com.himanshu.cameraintegrator.exceptions.RuntimePermissionNotGrantedException;

import java.io.File;
import java.io.IOException;


/**
 * Camera Intent Integrator simply removes the boiler plate code that need to generate
 * while using native camera app to click images
 *
 * @author Himanshu
 * @Example
 */
public class CameraIntegrator extends Integrator {

    private static final String INTENT_EXTRA_FILE_COMPLETE_PATH = "complete_path";
    private static final String INTENT_EXTRA_FILE_DIRECTORY_NAME = "camera_image_directory_name";
    private static final String INTENT_EXTRA_FILE_IMAGE_NAME = "image_name";
    private static final String INTENT_EXTRA_FILE_IMAGE_FORMAT = "format";
    private static final String INTENT_EXTRA_FINAL_REQUIRED_SIZE = "camera_required_size";

    /**
     * Activity Context
     */
    private Activity activityReference;


    private Fragment fragmentReference;

    /**
     * File instance on which we will be operating
     */
    private File mFile;

    /**
     * This Value can be given
     */
    private String imagePath;
    private String imageDirectoryName;
    private String imageName;
    private String imageFormat;

    /**
     * Required Size of the image
     * this can be one of the {@link ImagesSizes}
     */
    private int requiredImageSize;


    /**
     * @param activityReference path where the new image we clicked should be stored
     */
    public CameraIntegrator(Activity activityReference) {
        super(activityReference);
        this.activityReference = activityReference;
    }


    public CameraIntegrator(Fragment fragmentReference) {
        super(fragmentReference.getContext());
        this.fragmentReference = fragmentReference;
        this.activityReference = fragmentReference.getActivity();
    }

    /**
     * Setting the absolute path where image will be stored
     *
     * @param imagePath
     */
    public void setImagePath(@Nullable String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Setting the file where image will be stores
     *
     * @param mFile
     */
    public void setFile(File mFile) {
        this.mFile = mFile;
    }

    public void setImageDirectoryName(String directoryName) {
        this.imageDirectoryName = directoryName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageFormat(@ImageFormats.ImageFormat String format) {
        this.imageFormat = format;
    }

    public void setRequiredImageSize(@ImagesSizes.ImageSize int requiredImageSize) {
        this.requiredImageSize = requiredImageSize;
    }

    /**
     * Opens Camera for capturing image
     */
    public void initiateCapture() throws CameraActivityNotFoundException, IOException, RuntimePermissionNotGrantedException {

        // checking if storage permissions are granted or not if not then a RuntimePermissionNotGrantedException is thrown
        checkForStoragePermissions();

        //Lets create a file if user hasn't created one where image will be stored
        if (imagePath != null)
            mFile = new File(imagePath);
        else if (imageDirectoryName != null && imageName == null && imageFormat == null)
            mFile = ImageHelper.createImageFile(imageDirectoryName);
        else if (imageDirectoryName != null && imageName != null && imageFormat == null)
            mFile = ImageHelper.createImageFile(imageDirectoryName, imageName);
        else if (imageDirectoryName != null && imageName != null && imageFormat != null)
            mFile = ImageHelper.createImageFile(imageDirectoryName, imageName, imageFormat);

        if (mFile == null)
            throw new IOException("Details Where image should be stored are not provided, you can call ");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(activityReference.getPackageManager()) != null) {

            // Continue only if the File was successfully created
            if (mFile != null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                } else {
                    File file = new File(Uri.fromFile(mFile).getPath());
                    Uri photoUri = FileProvider.getUriForFile(activityReference.getApplicationContext(), activityReference.getPackageName() + ".provider", file);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                }

                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (fragmentReference == null)
                    activityReference.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                else
                    fragmentReference.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            }
        } else throw new CameraActivityNotFoundException("No Camera App found to initiate capture");
    }

    private void checkForStoragePermissions() throws RuntimePermissionNotGrantedException {

        Context context = (fragmentReference != null) ? fragmentReference.getContext() : activityReference;

        if (context == null)
            throw new IllegalStateException("Context cannot be null");

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            throw new RuntimePermissionNotGrantedException("storage permission not granted");
        }

    }

    @Override
    public void parseResults(int requestCode, int resultCode, Intent data, ImageCallback resultCallback) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (mFile != null) {
                getParsedBitmapResult(mFile, null, RequestSource.SOURCE_CAMERA, requiredImageSize, resultCallback);
            }
        }
    }

    @Override
    public void saveState(Bundle outState) {
        outState.putString(INTENT_EXTRA_FILE_COMPLETE_PATH, imagePath);
        outState.putString(INTENT_EXTRA_FILE_DIRECTORY_NAME, imageDirectoryName);
        outState.putString(INTENT_EXTRA_FILE_IMAGE_FORMAT, imageFormat);
        outState.putString(INTENT_EXTRA_FILE_IMAGE_NAME, imageName);
        outState.putInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, requiredImageSize);
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        imagePath = savedInstanceState.getString(INTENT_EXTRA_FILE_COMPLETE_PATH, null);
        imageDirectoryName = savedInstanceState.getString(INTENT_EXTRA_FILE_DIRECTORY_NAME, null);
        imageFormat = savedInstanceState.getString(INTENT_EXTRA_FILE_IMAGE_FORMAT, null);
        imageName = savedInstanceState.getString(INTENT_EXTRA_FILE_IMAGE_NAME, null);
        requiredImageSize = savedInstanceState.getInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, -1);
    }

}

