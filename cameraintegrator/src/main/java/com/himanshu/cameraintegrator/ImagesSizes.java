package com.himanshu.cameraintegrator;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Himanshu on 5/16/2018.
 */

public class ImagesSizes {

    /**
     * Represents an Image with width 128px
     */
    public static final int THUMBNAIL = 64;

    /**
     * Represents an Image with width 128px
     */
    public static final int THUMBNAIL_BIG = 128;


    /**
     * PNG (Portable Network Graphics) file
     */
    public static final int OPTIMUM_SMALL = 250;

    /**
     * PNG (Portable Network Graphics) file
     */
    public static final int OPTIMUM_MEDIUM = 500;

    /**
     * PNG (Portable Network Graphics) file
     */
    public static final int OPTIMUM_BIG = 750;

    /**
     * JPG or JPEG (Joint Photographic Experts Group) file
     */
    public static final int HALF_SIZE = 10000;

    /**
     * PNG (Portable Network Graphics) file
     */
    public static final int FULL_SIZE = 20000;
    private int width = 0;
    private int height = 0;

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

    // Declare the @StringDef for these constants
    @IntDef({THUMBNAIL, THUMBNAIL_BIG, OPTIMUM_MEDIUM, OPTIMUM_BIG, HALF_SIZE, FULL_SIZE, OPTIMUM_SMALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageSize {
    }
}
