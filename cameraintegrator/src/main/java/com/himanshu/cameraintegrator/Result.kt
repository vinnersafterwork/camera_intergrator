package com.himanshu.cameraintegrator

import android.graphics.Bitmap
import java.io.File

/**
 * Created by Himanshu on 4/30/2018.
 */
class Result {
    /**
     * Name of the image
     */
    var imageName: String? = null

    /**
     * Width of the resulting image
     */
    var width = 0

    /**
     * height of resulting image
     */
    var height = 0

    /**
     * File length in bytes
     */
    var fileLength: Long = 0

    /**
     * File length in MB(Mega Bytes)
     */
    var fileSizeInMb: Long = 0

    /**
     * Local Image Path
     */
    var imagePath: String? = null
    var bitmap: Bitmap? = null
    override fun toString(): String {
        return imagePath!!
    }

    fun dispose() {
        if (bitmap != null && !bitmap!!.isRecycled) bitmap!!.recycle()
        File(imagePath).delete()
    }
}