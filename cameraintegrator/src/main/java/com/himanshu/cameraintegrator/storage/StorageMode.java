package com.himanshu.cameraintegrator.storage;

public enum StorageMode {

    /**
     * Internal Storage is the storage which in exclusive to app,
     * no other app can access file present here
     */
    INTERNAL_STORAGE,

    /**
     * Internal cache Storage is the storage which in exclusive to app,
     * file to be stored here cannot be larger than 1 MB,
     * also Android Can remove file present here can be removed without notifying
     * <p>
     * Files present here can be deleted in various events like
     * <ul>
     * <li>System is low on storage</li>
     * <li>On Running App Like System Cleaner</li>
     * </ul>
     */
    INTERNAL_CACHE_STORAGE,

    /**
     * EXternal temporary Storage is the storage which in exclusive to app,
     * file to be stored here cannot be larger than 1 MB,
     * also Android Can remove file present here can be removed without notifying
     * <p>
     * Files present here can be deleted in various events like
     * <ul>
     * <li>System is low on storage</li>
     * <li>On Running App Like System Cleaner</li>
     * </ul>
     */
    EXTERNAL_CACHE_STORAGE,

    /**
     * External Storage is the storage which accessible to all the app,
     * Files present here can be viewed in app like File Manager,
     * Other apps can edit or delete file present here
     */
    EXTERNAL_PUBLIC_STORAGE
}
