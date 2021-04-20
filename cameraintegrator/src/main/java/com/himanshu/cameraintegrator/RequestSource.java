package com.himanshu.cameraintegrator;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Contains All the image formats that are supported by camera Integrator
 */
public class RequestSource {

    /**
     * JPG or JPEG (Joint Photographic Experts Group) file
     */
    public static final int SOURCE_CAMERA = 12;

    /**
     * PNG (Portable Network Graphics) file
     */
    public static final int SOURCE_GALLERY = 14;

    // Declare the @StringDef for these constants
    @IntDef({SOURCE_CAMERA, SOURCE_GALLERY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestSourceOptions {
    }


}
