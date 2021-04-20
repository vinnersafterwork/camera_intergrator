package com.himanshu.cameraintegrator.integrator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.himanshu.cameraintegrator.ImageCallback;
import com.himanshu.cameraintegrator.ImagesSizes;
import com.himanshu.cameraintegrator.RequestSource;
import com.himanshu.cameraintegrator.Result;
import com.himanshu.cameraintegrator.exceptions.CameraActivityNotFoundException;
import com.himanshu.cameraintegrator.exceptions.RuntimePermissionNotGrantedException;
import com.himanshu.cameraintegrator.storage.ImageStorageHelper;
import com.himanshu.cameraintegrator.storage.StorageMode;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * Camera Intent Integrator simply removes the boiler plate code that need to generate
 * while using native camera app to click images
 *
 * @author Himanshu
 */
public class CameraIntegrator extends Integrator {

    /**
     * Constant to be used for opening Image
     */
    public static final int REQUEST_IMAGE_CAPTURE = 21;

    private static final String INTENT_EXTRA_FILE_COMPLETE_PATH = "complete_path";
    private static final String INTENT_EXTRA_FILE_DIRECTORY_NAME = "camera_image_directory_name";
    private static final String INTENT_EXTRA_FILE_PUBLIC_DIRECTORY_NAME = "public_directory";
    private static final String INTENT_EXTRA_FILE_IMAGE_NAME = "image_name";
    private static final String INTENT_EXTRA_FILE_IMAGE_FORMAT = "format";
    private static final String INTENT_EXTRA_FINAL_REQUIRED_SIZE = "required_size";
    private static final String INTENT_EXTRA_STORAGE_MODE = "storage_mode";

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

    /**
     * Public Directory Name it can be
     * <ul>
     * <li>{@link Environment#DIRECTORY_MUSIC}</li>
     * <li>{@link Environment#DIRECTORY_PODCASTS}</li>
     * <li>{@link Environment#DIRECTORY_RINGTONES}</li>
     * <li>{@link Environment#DIRECTORY_ALARMS}</li>
     * <li>{@link Environment#DIRECTORY_NOTIFICATIONS}</li>
     * <li>{@link Environment#DIRECTORY_PICTURES}</li>     *
     * <li>{@link Environment#DIRECTORY_MOVIES}</li>
     * <li>{@link Environment#DIRECTORY_DOWNLOADS}</li>
     * <li>{@link Environment#DIRECTORY_DCIM}</li>
     * <li>{@link Environment#DIRECTORY_DOCUMENTS}</li>     *
     * </ul>
     */
    private @Nullable
    String publicDirectoryName;


    /**
     * Directory Name Where Image will be kept
     */
    private @Nullable
    String imageDirectoryName;

    private @Nullable
    String imageName;

    private @Nullable
    String imageFormat;

    /**
     * Required Size of the image
     * this can be one of the {@link ImagesSizes}
     */
    private @NonNull
    int requiredImageSize;


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
     * Sets Public Directory Name
     *
     * @param publicDirectoryName it can be
     *                            <ul>
     *                            <li>{@link Environment#DIRECTORY_MUSIC}</li>
     *                            <li>{@link Environment#DIRECTORY_PODCASTS}</li>
     *                            <li>{@link Environment#DIRECTORY_RINGTONES}</li>
     *                            <li>{@link Environment#DIRECTORY_ALARMS}</li>
     *                            <li>{@link Environment#DIRECTORY_NOTIFICATIONS}</li>
     *                            <li>{@link Environment#DIRECTORY_PICTURES}</li>
     *                            <li>{@link Environment#DIRECTORY_MOVIES}</li>
     *                            <li>{@link Environment#DIRECTORY_DOWNLOADS}</li>
     *                            <li>{@link Environment#DIRECTORY_DCIM}</li>
     *                            <li>{@link Environment#DIRECTORY_DOCUMENTS}</li>
     *                            </ul>
     */
    public void setPublicDirectoryName(String publicDirectoryName) {
        this.publicDirectoryName = publicDirectoryName;
    }

    /**
     * Setting the absolute path where image will be stored
     *
     * @param imagePath
     */
    public void setImagePath(@Nullable String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImageDirectoryName(String directoryName) {
        this.imageDirectoryName = directoryName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setRequiredImageSize(@ImagesSizes.ImageSize int requiredImageSize) {
        this.requiredImageSize = requiredImageSize;
    }

    /**
     * Opens Camera for capturing image
     */
    public void initiateCapture() throws CameraActivityNotFoundException, RuntimePermissionNotGrantedException {

        if (storageMode == StorageMode.EXTERNAL_PUBLIC_STORAGE || storageMode == StorageMode.EXTERNAL_CACHE_STORAGE) {
            checkForWriteStoragePermissions();
        }

        //Lets create a file if user hasn't created one where image will be stored
        if (imagePath != null)
            mFile = new File(imagePath);
        else
            mFile = createDestinationImageFile();


        if (mFile == null)
            throw new RuntimeException("Details Where image should be stored are not provided, you can call ");

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

    /**
     * Creates Destination Image File which will be passed to camera App, where Camera App will store the image
     *
     * @return
     */
    private File createDestinationImageFile() {

        StringBuilder imageDirectoryFinalPath = new StringBuilder();
        String imageFinalName;
        switch (storageMode) {
            case EXTERNAL_PUBLIC_STORAGE:

                if (publicDirectoryName != null)
                    imageDirectoryFinalPath.append(publicDirectoryName);

            case INTERNAL_CACHE_STORAGE:
            case INTERNAL_FILE_STORAGE:
            case EXTERNAL_CACHE_STORAGE:

                if (imageDirectoryFinalPath.length() != 0)
                    imageDirectoryFinalPath.append("/");

                if (imageDirectoryName != null)
                    imageDirectoryFinalPath.append(imageDirectoryName);
        }

        if (imageName != null) {
            imageFinalName = imageName.endsWith(".jpg") ? imageName : imageName + ".jpg";
        } else
            imageFinalName = ImageStorageHelper.createRandomImageFileName();


        File imageFile = null;

        switch (storageMode) {

            case INTERNAL_CACHE_STORAGE:
                imageFile = ImageStorageHelper.createCacheImageFile(mContext, imageDirectoryFinalPath.toString(), imageFinalName);
                break;

            case INTERNAL_FILE_STORAGE:
                imageFile = ImageStorageHelper.createInternalImageFile(mContext, imageDirectoryFinalPath.toString(), imageFinalName);
                break;

            case EXTERNAL_CACHE_STORAGE:
                imageFile = ImageStorageHelper.createExternalCacheImageFile(mContext, imageDirectoryFinalPath.toString(), imageFinalName);
                break;

            case EXTERNAL_FILE_STORAGE:
                imageFile = ImageStorageHelper.createExternalImageFile(mContext, imageDirectoryFinalPath.toString(), imageFinalName);
                break;

            case EXTERNAL_PUBLIC_STORAGE:
                imageFile = ImageStorageHelper.createExternalPublicImageFile(imageDirectoryFinalPath.toString(), imageFinalName);
                break;
        }


        return imageFile;
    }


    @Override
    public void parseResults(int requestCode, int resultCode, Intent data, ImageCallback resultCallback) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            if (mFile == null)
                return;

            taskExecutors.diskIO().execute(() -> {

                try {
                    Bitmap requiredSizeImage = getBitmapInRequiredSize(mFile, requiredImageSize);

                    //Now replacing mFile
                    // Saving the Bitmap of required size to the required directory
                    ImageStorageHelper.saveTo(mFile, requiredSizeImage);

                    //Preparing Results object
                    Result results = getResults(mFile, requiredSizeImage, RequestSource.SOURCE_CAMERA);

                    //Delivering Results back to main thread
                    taskExecutors.mainThread().execute(() ->
                    {
                        resultCallback.onResult(RequestSource.SOURCE_CAMERA, results, null);
                        removeUsedFields();
                    });

                } catch (FileNotFoundException e) {
                    taskExecutors.mainThread().execute(() ->
                    {
                        resultCallback.onResult(RequestSource.SOURCE_CAMERA, null, e);
                        removeUsedFields();
                    });
                }

            });
        }
    }

    private void removeUsedFields() {
        imagePath = null;
        mFile = null;
    }

    @Override
    public void saveState(Bundle outState) {

        if (mFile != null)
            outState.putString(INTENT_EXTRA_FILE_COMPLETE_PATH, mFile.getAbsolutePath());

        outState.putString(INTENT_EXTRA_FILE_DIRECTORY_NAME, imageDirectoryName);
        outState.putString(INTENT_EXTRA_FILE_IMAGE_FORMAT, imageFormat);
        outState.putString(INTENT_EXTRA_FILE_IMAGE_NAME, imageName);
        outState.putInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, requiredImageSize);
        outState.putString(INTENT_EXTRA_STORAGE_MODE, storageMode.name());
        outState.putString(INTENT_EXTRA_FILE_PUBLIC_DIRECTORY_NAME, publicDirectoryName);

    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        imagePath = savedInstanceState.getString(INTENT_EXTRA_FILE_COMPLETE_PATH, null);
        imageDirectoryName = savedInstanceState.getString(INTENT_EXTRA_FILE_DIRECTORY_NAME, null);
        imageFormat = savedInstanceState.getString(INTENT_EXTRA_FILE_IMAGE_FORMAT, null);
        imageName = savedInstanceState.getString(INTENT_EXTRA_FILE_IMAGE_NAME, null);
        requiredImageSize = savedInstanceState.getInt(INTENT_EXTRA_FINAL_REQUIRED_SIZE, -1);
        publicDirectoryName = savedInstanceState.getString(INTENT_EXTRA_FILE_PUBLIC_DIRECTORY_NAME);

        String storageModeString = savedInstanceState.getString(INTENT_EXTRA_STORAGE_MODE);
        if (storageModeString != null)
            storageMode = StorageMode.valueOf(storageModeString);

        if (imagePath != null)
            mFile = new File(imagePath);
    }

}

