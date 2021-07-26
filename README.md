[![Release](https://jitpack.io/v/vinnersafterwork/camera_intergrator.svg)](https://jitpack.io/#vinnersafterwork/camera_intergrator)


## Under Development *Not Ready For Production Yet*

# camera_intergrator
A Light Camera and Galllery utility that helps you in reducing boilerplate code using Default camera app or while picking image from gallery

### Usage

#### Setup

Step 1. Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.vinnersafterwork:camera_intergrator:0.3.3'
	}
```


#### 1. Using Camera

1. Start Camera Integrator
```
cameraIntegrator = CameraIntegrator(this)
cameraIntegrator.setStorageMode(StorageMode.INTERNAL_FILE_STORAGE) //Mandatory
cameraIntegrator.setImageDirectoryName("Folder101") //Optional
cameraIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM) //Optional
 
cameraIntegrator.initiateCapture()
```

2. Look for Results at ```OnActivityResult```

```
 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CameraIntegrator.REQUEST_IMAGE_CAPTURE)
            cameraIntegrator.parseResults(requestCode, resultCode, data, camerResultCallback)
    }
```

3. Use the Image User just Clicked

```
 private val camerResultCallback = ImageCallback { requestedBy, result, error ->

        Glide.with(this).load(result!!.bitmap).into(iv)
        text_path.text = "${result.imagePath}"
    }
```


#### 2. Picking Image

1. Start Gallery Integrator
```
 galleryIntegrator = GalleryIntegrator(this)
 galleryIntegrator.setRequiredImageSize(ImagesSizes.OPTIMUM_MEDIUM)
 galleryIntegrator.setStorageMode(StorageMode.EXTERNAL_PUBLIC_STORAGE)
 galleryIntegrator.setPublicDirectoryName(Environment.DIRECTORY_DCIM)
 galleryIntegrator.setImageDirectoryName("Folder101")

 galleryIntegrator.initiateImagePick()

```

2. Look for Results at ```OnActivityResult```

```
 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GalleryIntegrator.REQUEST_IMAGE_PICK)
            galleryIntegrator.parseResults(requestCode, resultCode, data, camerResultCallback)
    }
```

3. Use the Image User just Clicked

```
 private val camerResultCallback = ImageCallback { requestedBy, result, error ->

        Glide.with(this).load(result!!.bitmap).into(iv)
        text_path.text = "${result.imagePath}"
    }
```
