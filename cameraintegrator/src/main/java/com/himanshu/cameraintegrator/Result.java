package com.himanshu.cameraintegrator;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by Himanshu on 4/30/2018.
 */

public class Result {

    /**
     * Name of the image
     */
    private String imageName;

    /**
     * Width of the resulting image
     */
    private int width;

    /**
     * height of resulting image
     */
    private int height;

    /**
     * File length in bytes
     */
    private long fileLength;

    /**
     * File length in MB(Mega Bytes)
     */
    private long fileSizeInMb;

    /**
     * Local Image Path
     */
    private String imagePath;

    private Bitmap bitmap;

    @Override
    public String toString() {
        return imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getFileSizeInMb() {
        return fileSizeInMb;
    }

    public void setFileSizeInMb(long fileSizeInMb) {
        this.fileSizeInMb = fileSizeInMb;
    }

    public @Nullable
    String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void dispose() {

        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();

        new File(imagePath).delete();
    }
}
