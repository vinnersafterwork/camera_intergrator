package com.himanshu.cameraintegrator_example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.himanshu.cameraintegrator.ImageCallback
import com.himanshu.cameraintegrator.ImagesSizes
import com.himanshu.cameraintegrator.Result
import com.himanshu.cameraintegrator.image_editor.EditImageActivity
import com.himanshu.cameraintegrator.integrator.CameraIntegrator
import com.himanshu.cameraintegrator.integrator.GalleryIntegrator
import com.himanshu.cameraintegrator.storage.StorageMode
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {


    lateinit var galleryIntegrator: GalleryIntegrator
    lateinit var cameraIntegrator: CameraIntegrator

    private val camerResultCallback = object : ImageCallback {

        override fun onResult(requestedBy: Int, result: Result?, error: Throwable?) {

            val image = File(result!!.imagePath)
            val intent = Intent(this@MainActivity, EditImageActivity::class.java)
            intent.putExtra(EditImageActivity.INTENT_EXTRA_FINAL_TO_EDIT_URI, Uri.fromFile(image))
            startActivityForResult(intent, REQUEST_CODE_EDIT_IMAGE)

//            Glide.with(this@MainActivity).load(result!!.bitmap).into(iv)
//            text_path.text = "${result.imagePath}"
        }
    }

    private fun storagePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun askForStoragePerm() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        requestPermissions(permissions, 23)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setCameraListeners()
        setGalleryListeners()

    }

    private fun setCameraListeners() {
        camera_internal_files.setOnClickListener {

            cameraIntegrator = CameraIntegrator(this)
            //Mandatory
            cameraIntegrator.setStorageMode(StorageMode.INTERNAL_FILE_STORAGE)

            //Optional
            cameraIntegrator.setImageDirectoryName("Humddaa")
            cameraIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)

            cameraIntegrator.initiateCapture()
        }


        camera_external_cache.setOnClickListener {

            if (!storagePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForStoragePerm()
                }
                return@setOnClickListener
            }

            cameraIntegrator = CameraIntegrator(this)
            //Mandatory
            cameraIntegrator.setStorageMode(StorageMode.EXTERNAL_CACHE_STORAGE)

            //Optional
            cameraIntegrator.setImageDirectoryName("Hudddmaa")
            cameraIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            cameraIntegrator.initiateCapture()

        }

        camera_external_file.setOnClickListener {

            if (!storagePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForStoragePerm()
                }
                return@setOnClickListener
            }

            cameraIntegrator = CameraIntegrator(this)
            //Mandatory
            cameraIntegrator.setStorageMode(StorageMode.EXTERNAL_FILE_STORAGE)

            //Optional
            cameraIntegrator.setImageDirectoryName("Hudddmaa")
            cameraIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            cameraIntegrator.initiateCapture()

        }

        camera_external_public.setOnClickListener {

            if (!storagePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForStoragePerm()
                }
                return@setOnClickListener
            }


            cameraIntegrator = CameraIntegrator(this)
            //Mandatory
            cameraIntegrator.setStorageMode(StorageMode.EXTERNAL_PUBLIC_STORAGE)


            //Optional
            cameraIntegrator.setPublicDirectoryName(Environment.DIRECTORY_DCIM)
            cameraIntegrator.setImageDirectoryName("Hudddmaa")
            cameraIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            cameraIntegrator.initiateCapture()

        }
    }


    private fun setGalleryListeners() {

        gallery_internal_cache.setOnClickListener {

            galleryIntegrator = GalleryIntegrator(this)
            galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            galleryIntegrator.setStorageMode(StorageMode.INTERNAL_CACHE_STORAGE)
            galleryIntegrator.setImageDirectoryName("Folder101")

            galleryIntegrator.initiateImagePick()

        }

        gallery_internal_files.setOnClickListener {

            galleryIntegrator = GalleryIntegrator(this)
            galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            galleryIntegrator.setStorageMode(StorageMode.INTERNAL_FILE_STORAGE)
            galleryIntegrator.setImageDirectoryName("Folder101")

            galleryIntegrator.initiateImagePick()
        }


        gallery_external_cache.setOnClickListener {

            //Read Storage Perm Required

            if (!storagePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForStoragePerm()
                }
                return@setOnClickListener
            }

            galleryIntegrator = GalleryIntegrator(this)
            galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            galleryIntegrator.setStorageMode(StorageMode.EXTERNAL_CACHE_STORAGE)
            galleryIntegrator.setImageDirectoryName("Folder101")

            galleryIntegrator.initiateImagePick()
        }

        gallery_external_file.setOnClickListener {

            if (!storagePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForStoragePerm()
                }
                return@setOnClickListener
            }

            galleryIntegrator = GalleryIntegrator(this)
            galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            galleryIntegrator.setStorageMode(StorageMode.EXTERNAL_FILE_STORAGE)
            galleryIntegrator.setImageDirectoryName("Folder101")

            galleryIntegrator.initiateImagePick()

        }

        gallery_external_public.setOnClickListener {

            if (!storagePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForStoragePerm()
                }
                return@setOnClickListener
            }


            galleryIntegrator = GalleryIntegrator(this)
            galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
            galleryIntegrator.setStorageMode(StorageMode.EXTERNAL_PUBLIC_STORAGE)
            galleryIntegrator.setPublicDirectoryName(Environment.DIRECTORY_DCIM)
            galleryIntegrator.setImageDirectoryName("Folder101")

            galleryIntegrator.initiateImagePick()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == GalleryIntegrator.REQUEST_IMAGE_PICK)
            galleryIntegrator.parseResults(requestCode, resultCode, data!!, camerResultCallback)
        else if (requestCode == CameraIntegrator.REQUEST_IMAGE_CAPTURE)
            cameraIntegrator.parseResults(requestCode, resultCode, data, camerResultCallback)
        else if (requestCode == REQUEST_CODE_EDIT_IMAGE) {

            val outputImageUri = data?.data ?: return
            Glide.with(this@MainActivity).load(outputImageUri).into(iv)
            text_path.text = outputImageUri.path
        }
    }


    companion object {

        const val REQUEST_CODE_EDIT_IMAGE = 2311
    }
}
