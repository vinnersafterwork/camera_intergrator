package com.himanshu.cameraintegrator;


import androidx.annotation.Nullable;

/**
 * Interface that will be used to to deliver results back
 */
public interface ImageCallback {

    /**
     * Invoked for a delivering image results back
     */
    void onResult(@RequestSource.RequestSourceOptions int requestedBy, @Nullable Result result, @Nullable Throwable error);

}
