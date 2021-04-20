package com.himanshu.cameraintegrator;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Contains All the image formats that are supported by camera Integrator
 */
public class ImageFormats {

    /**
     * JPG or JPEG (Joint Photographic Experts Group) file
     */
    public static final String JPEG = "jpg";

    /**
     * PNG (Portable Network Graphics) file
     */
    public static final String PNG = "png";

    // Declare the @StringDef for these constants
    @StringDef({JPEG, PNG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageFormat {
    }

}
