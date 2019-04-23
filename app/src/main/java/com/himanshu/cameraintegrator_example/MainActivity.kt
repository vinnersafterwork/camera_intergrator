package com.himanshu.cameraintegrator_example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.himanshu.cameraintegrator.ImageCallback
import com.himanshu.cameraintegrator.ImagesSizes
import com.himanshu.cameraintegrator.integrator.CameraIntegrator
import com.himanshu.cameraintegrator.integrator.GalleryIntegrator
import com.himanshu.cameraintegrator.storage.StorageMode
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    lateinit var galleryIntegrator: GalleryIntegrator
    lateinit var cameraIntegrator: CameraIntegrator

    private val camerResultCallback = ImageCallback { requestedBy, result, error ->

        Glide.with(this).load(result!!.bitmap).into(iv)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

//        cameraIntegrator = CameraIntegrator(this)
//        cameraIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
//        cameraIntegrator.setStorageMode(StorageMode.INTERNAL_CACHE_STORAGE)
//
//        cameraIntegrator.setImagePath()
        //     cameraIntegrator.setImageDirectoryName("cameraResult")


        galleryIntegrator = GalleryIntegrator(this)
        galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
        galleryIntegrator.setStorageMode(StorageMode.EXTERNAL_PUBLIC_STORAGE)
        galleryIntegrator.setImageDirectoryName("Folder101")

        galleryBtn.setOnClickListener { galleryIntegrator.initiateImagePick() }

//        cameraBtn.setOnClickListener { cameraIntegrator.initiateCapture() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GalleryIntegrator.REQUEST_IMAGE_PICK)
            galleryIntegrator.parseResults(requestCode, resultCode, data, camerResultCallback)

    }


}
