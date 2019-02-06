package com.himanshu.cameraintegrator;

/**
 * Interface that will be used to to deliver results back
 */
public interface ImageCallback {

    /**
     * Invoked for a delivering image results back
     */
    void onResult(@RequestSource.RequestSourceOptions int requestedBy, Result result);
}
