package com.himanshu.cameraintegrator

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Contains All the image formats that are supported by camera Integrator
 */
object RequestSource {
    /**
     * JPG or JPEG (Joint Photographic Experts Group) file
     */
    const val SOURCE_CAMERA = 12

    /**
     * PNG (Portable Network Graphics) file
     */
    const val SOURCE_GALLERY = 14

    // Declare the @StringDef for these constants
    @IntDef(SOURCE_CAMERA, SOURCE_GALLERY)
    @Retention(RetentionPolicy.SOURCE)
    annotation class RequestSourceOptions
}