package com.himanshu.cameraintegrator

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by Himanshu on 5/16/2018.
 */
class ImagesSizes {
    var width = 0
    var height = 0

    // Declare the @StringDef for these constants
    @IntDef(
        THUMBNAIL,
        THUMBNAIL_BIG,
        OPTIMUM_MEDIUM,
        OPTIMUM_BIG,
        HALF_SIZE,
        FULL_SIZE,
        OPTIMUM_SMALL
    )
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class ImageSize
    companion object {
        /**
         * Represents an Image with width 128px
         */
        const val THUMBNAIL = 64

        /**
         * Represents an Image with width 128px
         */
        const val THUMBNAIL_BIG = 128

        /**
         * PNG (Portable Network Graphics) file
         */
        const val OPTIMUM_SMALL = 250

        /**
         * PNG (Portable Network Graphics) file
         */
        const val OPTIMUM_MEDIUM = 500

        /**
         * PNG (Portable Network Graphics) file
         */
        const val OPTIMUM_BIG = 750

        /**
         * JPG or JPEG (Joint Photographic Experts Group) file
         */
        const val HALF_SIZE = 10000

        /**
         * PNG (Portable Network Graphics) file
         */
        const val FULL_SIZE = 20000
    }
}