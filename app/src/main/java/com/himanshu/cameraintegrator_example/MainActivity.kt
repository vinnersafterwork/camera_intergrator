package com.himanshu.cameraintegrator_example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.himanshu.cameraintegrator.ImageCallback
import com.himanshu.cameraintegrator.ImagesSizes
import com.himanshu.cameraintegrator.integrator.CameraIntegrator
import com.himanshu.cameraintegrator.integrator.GalleryIntegrator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    lateinit var galleryIntegrator: GalleryIntegrator
    lateinit var cameraIntegrator: CameraIntegrator

    private val camerResultCallback = ImageCallback { requestedBy, result ->
        Glide.with(this).load(result.bitmap).into(iv)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraIntegrator = CameraIntegrator(this)
        cameraIntegrator.setImageDirectoryName("cameraResult")
        cameraIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)


        galleryIntegrator = GalleryIntegrator(this)
        galleryIntegrator.setImageDirectoryName("galleryResult")
        galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)

        galleryBtn.setOnClickListener { galleryIntegrator.initiateImagePick() }

        cameraBtn.setOnClickListener { cameraIntegrator.initiateCapture() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CameraIntegrator.REQUEST_IMAGE_CAPTURE)
                cameraIntegrator.parseResults(requestCode, resultCode, data, camerResultCallback)
            else if (requestCode == CameraIntegrator.REQUEST_IMAGE_PICK)
                galleryIntegrator.parseResults(requestCode, resultCode, data, camerResultCallback)
        }
    }


}
