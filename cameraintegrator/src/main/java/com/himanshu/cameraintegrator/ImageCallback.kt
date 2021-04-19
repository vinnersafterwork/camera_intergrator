package com.himanshu.cameraintegrator

import com.himanshu.cameraintegrator.RequestSource.RequestSourceOptions
import java.io.File

/**
 * Interface that will be used to to deliver results back
 */
interface ImageCallback {
    /**
     * Invoked for a delivering image results back
     */
    fun onResult(@RequestSourceOptions requestedBy: Int, result: Result?, error: Throwable?)
}