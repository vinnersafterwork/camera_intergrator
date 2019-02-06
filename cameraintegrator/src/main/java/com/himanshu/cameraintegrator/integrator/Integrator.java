package com.himanshu.cameraintegrator.integrator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.himanshu.cameraintegrator.*;
import com.himanshu.cameraintegrator.executors.AppExecutors;

import java.io.File;
import java.util.concurrent.ExecutionException;


/**
 * Base Integrator
 */
public abstract class Integrator {

    /**
     * Constant to be used for identifying image capture action
     */
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Constant to be used for identifying image pick action
     */
    public static final int REQUEST_IMAGE_PICK = 2;
    Result imageCaptureResult;
    /**
     * {@link AppExecutors} for switching threads
     */
    private AppExecutors taskExecutors;
    /**
     * Context of calling activity
     */
    private Context context;

    public Integrator(Context context) {
        this.context = context;
        taskExecutors = AppExecutors.getInstance();
    }

    /**
     * Reads the Image File without loading it onto memory and returns
     * information about the image file
     *
     * @param mFile
     * @return
     */
    protected BitmapFactory.Options getImageMetaData(File mFile) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);

        return options;
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
                    with(context)
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
     * For Saving State in Case activity got destroyed by Android
     *
     * @param outState
     */
    abstract void saveState(Bundle outState);

    /**
     * For Restoring state in case of activity or fragment regenration
     */
    abstract void restoreState(Bundle savedInstanceState);


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

    protected void getParsedBitmapResult(File mFile, @Nullable String targetFolderName, @RequestSource.RequestSourceOptions final int source, final int requiredImageSize, final ImageCallback callback) {

        Runnable getImageInfoTask = () -> {

            //Reading data About the File
            BitmapFactory.Options sourceImageData = getImageMetaData(mFile);

            //Bitmap Of Requried Size
            final Bitmap requiredSizeImage = getRequiredSizeImage(mFile, sourceImageData, requiredImageSize);

            //Creating A Copy Of Selected File In Case of Gallery Pick
            //and replacing the large file with small file in Camera Capture

            imageCaptureResult = new Result();
            imageCaptureResult.setWidth(requiredSizeImage.getWidth());
            imageCaptureResult.setHeight(requiredSizeImage.getHeight());
            imageCaptureResult.setBitmap(requiredSizeImage);

            if (source == RequestSource.SOURCE_CAMERA) {

                //Replacing the Original Large File captured By the camera
                //with the size of file we needed
                ImageHelper.saveTo(mFile.getAbsolutePath(), requiredSizeImage);
                imageCaptureResult.setImageName(mFile.getName());
                imageCaptureResult.setImagePath(mFile.getAbsolutePath());
                imageCaptureResult.setFileSizeInMb(mFile.length() / (1024 * 1024));

            } else if (source == RequestSource.SOURCE_GALLERY) {

                //Creating A Copy of required Size Image File
                //and then storing it in required folder
                File galleryPickedFile = ImageHelper.createImageFile(targetFolderName);
                ImageHelper.saveTo(galleryPickedFile.getAbsolutePath(), requiredSizeImage);
                imageCaptureResult.setImageName(galleryPickedFile.getName());
                imageCaptureResult.setImagePath(galleryPickedFile.getAbsolutePath());
                imageCaptureResult.setFileSizeInMb(galleryPickedFile.length() / (1024 * 1024));
            }

            taskExecutors.mainThread().execute(() -> callback.onResult(source, imageCaptureResult));
        };

        taskExecutors.diskIO().execute(getImageInfoTask);

    }


}
