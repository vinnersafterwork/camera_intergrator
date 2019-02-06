package com.himanshu.cameraintegrator.integrator;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;

import android.support.v4.content.ContextCompat;
import com.himanshu.cameraintegrator.ImageCallback;
import com.himanshu.cameraintegrator.ImagesSizes;
import com.himanshu.cameraintegrator.RequestSource;
import com.himanshu.cameraintegrator.exceptions.RuntimePermissionNotGrantedException;


/**
 * Created by Himanshu on 4/30/2018.
 */

public class GalleryIntegrator extends Integrator {

    /**
     * Activity Context
     */
    private Activity activityRef;

    /**
     * Refrence of calling Activity
     */
    private Fragment fragmentReference;
    /**
     * File instance on which we will be operating
     */
    private File mFile;
    private String imageDirectoryName;


    /**
     * Required Size of the image
     * this can be one of the {@link ImagesSizes}
     */
    private int requiredImageSize;

    /**
     * @param activityRef path where the new image we clicked should be stored
     */
    public GalleryIntegrator(Activity activityRef) {
        super(activityRef);
        this.activityRef = activityRef;
    }

    public GalleryIntegrator(Fragment fragmentReference) {
        super(fragmentReference.getActivity());
        this.fragmentReference = fragmentReference;
        this.activityRef = fragmentReference.getActivity();
    }

    public void setRequiredImageSize(@ImagesSizes.ImageSize int requiredImageSize) {
        this.requiredImageSize = requiredImageSize;
    }


    public void setImageDirectoryName(String directoryName) {
        this.imageDirectoryName = directoryName;
    }

    /**
     * Initiates Image Pick Process
     *
     * @throws ActivityNotFoundException if There is no application to pick image
     */
    public void initiateImagePick() throws ActivityNotFoundException, RuntimePermissionNotGrantedException {

        checkForStoragePermissions();

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

        if (fragmentReference == null)
            activityRef.startActivityForResult(intent, REQUEST_IMAGE_PICK);
        else
            fragmentReference.startActivityForResult(intent, REQUEST_IMAGE_PICK);


    }

    private void checkForStoragePermissions() throws RuntimePermissionNotGrantedException{

        Context context = (fragmentReference != null ) ? fragmentReference.getContext() : activityRef;

        if(context == null)
            throw new IllegalStateException("Context cannot be null");

        if (ContextCompat.checkSelfPermission(context, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimePermissionNotGrantedException("storage permission not granted");
        }

    }

    @Override
    public void parseResults(int requestCode, int resultCode, Intent data, ImageCallback resultCallback) {

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                String imagePath = getRealPathFromURI(activityRef, selectedImageUri);
                if (imagePath != null) {
                    mFile = new File(imagePath);
                    getParsedBitmapResult(mFile, imageDirectoryName, RequestSource.SOURCE_GALLERY, requiredImageSize, resultCallback);
                }
            }
        }

    }


    /**
     * Parses #contentURI and returns Absolute path of image
     *
     * @param c
     * @param contentURI
     * @return
     */
    private String getRealPathFromURI(Context c, Uri contentURI) {
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