package com.himanshu.cameraintegrator.image_editor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.himanshu.cameraintegrator.R
import com.himanshu.cameraintegrator.adapters.EditingToolsAdapter
import com.himanshu.cameraintegrator.adapters.EditingToolsAdapter.OnItemSelected
import com.himanshu.cameraintegrator.adapters.FilterListener
import com.himanshu.cameraintegrator.adapters.ToolType
import com.himanshu.cameraintegrator.base.BaseActivity
import com.himanshu.cameraintegrator.fragments.PropertiesBSFragment
import com.himanshu.cameraintegrator.fragments.StickerBSFragment
import com.himanshu.cameraintegrator.fragments.StickerBSFragment.StickerListener
import com.himanshu.cameraintegrator.fragments.TextEditorDialogFragment
import com.himanshu.cameraintegrator.storage.ImageStorageHelper.createInternalImageFile
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener

class EditImageActivity : BaseActivity(), OnPhotoEditorListener, PropertiesBSFragment.Properties,
    StickerListener, OnItemSelected, FilterListener {

    //Ui Views

    private lateinit var mPhotoEditor: PhotoEditor
    private lateinit var imgUndo: ImageView
    private lateinit var imgRedo: ImageView
    private lateinit var imgSave: ImageView
    private lateinit var imgClose: ImageView
    private lateinit var mTxtCurrentTool: TextView
    private lateinit var mRvTools: RecyclerView
    private lateinit var mRvFilters: RecyclerView

    private lateinit var mRootView: ConstraintLayout
    private val mConstraintSet = ConstraintSet()

    //Data
    private var mFinalImageUri: Uri? = null
    private lateinit var mImageToEditUri: Uri

    //Bottom sheet fragments
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    //    private EmojiBSFragment mEmojiBSFragment;

    //Adapters
    private val mEditingToolsAdapter = EditingToolsAdapter(this)

    //    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private var mIsFilterVisible = false
    private lateinit var mPhotoEditorView: PhotoEditorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_image)
        initViews()
        getDataFromIntentAndSavedInstance(intent, savedInstanceState)
        setOnClickListener()

//      mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");
        mPropertiesBSFragment = PropertiesBSFragment()
        //        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        //        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)
        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools.layoutManager = llmTools
        mRvTools.adapter = mEditingToolsAdapter

//        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        mRvFilters.setLayoutManager(llmFilters);
//        mRvFilters.setAdapter(mFilterViewAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk
        mPhotoEditor.setOnPhotoEditorListener(this)

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
        setImageOnEditorView()
    }

    private fun getDataFromIntentAndSavedInstance(intent: Intent?, savedInstanceState: Bundle?) {
        if (intent != null) {
            mImageToEditUri = intent.getParcelableExtra(INTENT_EXTRA_FINAL_TO_EDIT_URI)
        }
        if (savedInstanceState != null) {
            mImageToEditUri = savedInstanceState.getParcelable(INTENT_EXTRA_FINAL_TO_EDIT_URI)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(INTENT_EXTRA_FINAL_TO_EDIT_URI, mImageToEditUri)
    }

    private fun setImageOnEditorView() {
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasStoragePermission) {
            //todo finish
            return
        }
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageToEditUri)
            mPhotoEditorView.source.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initViews() {
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        mRootView = findViewById(R.id.rootView)
    }

    private fun setOnClickListener() {
        imgClose = findViewById(R.id.imgClose)
        imgClose.setOnClickListener { onBackPressed() }

        imgUndo = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener { mPhotoEditor.undo() }

        imgRedo = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener { mPhotoEditor.redo() }

        imgSave = findViewById(R.id.imgSave)
        imgSave.setOnClickListener { saveImage() }
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener { inputText, outputColorCode ->
            val styleBuilder = TextStyleBuilder()
            styleBuilder.withTextColor(outputColorCode)
            mPhotoEditor.editText(rootView, inputText, styleBuilder)
            mTxtCurrentTool.setText(R.string.label_text)
        }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    private fun saveImage() {
        if (mFinalImageUri == null) {
            val fileName = System.currentTimeMillis().toString() + ".png"
            val finalImageFile = createInternalImageFile(this, "Edited_Images", fileName)
            mFinalImageUri = Uri.fromFile(finalImageFile)
        } else {
            val hasStoragePermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasStoragePermission) {


                //todo finish
                return
            }
        }
        val saveSettings = SaveSettings.Builder()
            .setClearViewsEnabled(true)
            .setTransparencyEnabled(true)
            .build()
        mPhotoEditor.saveAsFile(mFinalImageUri?.path!!, saveSettings, object : OnSaveListener {
            override fun onSuccess(imagePath: String) {
                val i = Intent()
                i.data = mFinalImageUri
                setResult(RESULT_OK, i)
                finish()
            }

            override fun onFailure(exception: Exception) {
                hideLoading()
                showSnackbar("Failed to save Image")
            }
        })
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor.brushColor = colorCode
        mTxtCurrentTool.setText(R.string.label_brush)
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor.setOpacity(opacity)
        mTxtCurrentTool.setText(R.string.label_brush)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor.brushSize = brushSize.toFloat()
        mTxtCurrentTool.setText(R.string.label_brush)
    }

    override fun onStickerClick(bitmap: Bitmap) {
        mPhotoEditor.addImage(bitmap)
        mTxtCurrentTool.setText(R.string.label_sticker)
    }

    override fun isPermissionGranted(isGranted: Boolean, permission: String) {
        if (isGranted) {
            saveImage()
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.msg_save_image))
        builder.setPositiveButton("Save") { _, _ -> saveImage() }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.setNeutralButton("Discard") { _, _ -> finish() }
        builder.create().show()
    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter)
    }

    override fun onToolSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.BRUSH -> {

                mPhotoEditor.setBrushDrawingMode(true)
                mTxtCurrentTool.setText(R.string.label_brush)
                showBottomSheetDialogFragment(mPropertiesBSFragment)
            }
            ToolType.TEXT -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    mPhotoEditor.addText(inputText, styleBuilder)
                    mTxtCurrentTool.setText(R.string.label_text)
                }
            }
            ToolType.ERASER -> {

                mPhotoEditor.brushEraser()
                mTxtCurrentTool.setText(R.string.label_eraser_mode)
            }
            ToolType.FILTER -> {
                mTxtCurrentTool.setText(R.string.label_filter)
                showFilter(true)
            }
            ToolType.STICKER -> showBottomSheetDialogFragment(mStickerBSFragment)
            else -> {

            }
        }
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(mRvFilters.id, ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                mRvFilters.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(mRvFilters.id, ConstraintSet.END)
        }
        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        TransitionManager.beginDelayedTransition(mRootView, changeBounds)
        mConstraintSet.applyTo(mRootView)
    }

    override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            mTxtCurrentTool.text = getString(R.string.image_editor)
        } else if (!mPhotoEditor.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "EditImageActivity"
        const val INTENT_EXTRA_FINAL_TO_EDIT_URI = "file_to_edit_uri"
    }
}