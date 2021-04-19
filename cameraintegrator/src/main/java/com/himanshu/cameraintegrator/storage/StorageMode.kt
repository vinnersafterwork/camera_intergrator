package com.himanshu.cameraintegrator.storage

enum class StorageMode {
    /**
     * Internal cache Storage is the storage which in exclusive to app,
     * file to be stored here cannot be larger than 1 MB,
     * also Android Can remove file present here can be removed without notifying
     *
     *
     * Files present here can be deleted in various events like
     *
     *  * System is low on storage
     *
     */
    INTERNAL_CACHE_STORAGE,

    /**
     * Internal Storage is the storage which in exclusive to app,
     * no other app can access file present here
     */
    INTERNAL_FILE_STORAGE,

    /**
     * External temporary Storage is the storage which in exclusive to app,
     * file to be stored here cannot be larger than 1 MB,
     * also Android Can remove file present here can be removed without notifying
     *
     *
     * Files present here can be deleted in various events like
     *
     *  * System is low on storage
     *  * On Running App Like System Cleaner
     *
     */
    EXTERNAL_CACHE_STORAGE,

    /**
     * External Storage is the storage which in exclusive to app,
     * no other app can access file present here
     */
    EXTERNAL_FILE_STORAGE,

    /**
     * External Storage is the storage which accessible to all the app,
     * Files present here can be viewed in app like File Manager,
     * Other apps can edit or delete file present here
     */
    EXTERNAL_PUBLIC_STORAGE
}