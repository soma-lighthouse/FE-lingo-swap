package com.lighthouse.auth.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.AspectRatioStrategy.FALLBACK_RULE_AUTO
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentCameraBinding
import com.lighthouse.auth.util.ANIMATION_FAST_MILLIS
import com.lighthouse.auth.util.ANIMATION_SLOW_MILLIS
import com.lighthouse.auth.util.createFile
import com.lighthouse.auth.util.getOutputDirectory
import com.lighthouse.auth.util.hasPermissions
import com.lighthouse.auth.util.simulateClick
import com.lighthouse.auth.view.KEY_EVENT_ACTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.util.ArrayDeque
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

val EXTENSION_WHITELIST = arrayOf("JPG", "PNG")
const val PHOTO_EXTENSION = ".jpg"
const val RATIO_4_3_VALUE = 4.0 / 3.0
const val RATIO_16_9_VALUE = 16.0 / 9.0
typealias LumaListener = (luma: Double) -> Unit

class CameraFragment : BindingFragment<FragmentCameraBinding>(R.layout.fragment_camera) {
    private lateinit var mContainer: ConstraintLayout
    private lateinit var mOutputDirectory: File
    private lateinit var mLocalBroadcastManager: LocalBroadcastManager
    private lateinit var cameraExecutor: ExecutorService

    private var mDisplayId: Int = -1
    private var mDefaultLen: Int = CameraSelector.LENS_FACING_BACK
    private var mCameraPreview: Preview? = null
    private var mImageCapture: ImageCapture? = null
    private var mImageAnalyzer: ImageAnalysis? = null
    private var mCamera: Camera? = null
    private var preview: Preview? = null

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    // Volume down button used to trigger shutter
    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra("key_event_extra", KeyEvent.KEYCODE_UNKNOWN)) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    binding.cameraCaptureButton.simulateClick()
                }
            }
        }
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.mDisplayId) {
                Log.d("CAMERA", "Rotation changed: ${view.display.rotation}")
                mImageCapture?.targetRotation = view.display.rotation
                mImageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermissions(requireContext())) {
            Navigation.findNavController(
                requireActivity(),
                R.id.fragment_container
            ).navigate(
                CameraFragmentDirections.actionCameraToPermissions()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        mLocalBroadcastManager.unregisterReceiver(volumeDownReceiver)
        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContainer = view as ConstraintLayout
        cameraExecutor = Executors.newSingleThreadExecutor()
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(view.context)
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        mLocalBroadcastManager.registerReceiver(volumeDownReceiver, filter)
        displayManager.registerDisplayListener(displayListener, null)
        mOutputDirectory = getOutputDirectory(requireContext())
        binding.viewFinder.post {
            mDisplayId = binding.viewFinder.display.displayId
            updateCameraUi()
            bindCameraUseCases()
        }
    }

    private fun setGalleryThumbnail(uri: Uri) {
        binding.photoViewButton.post {
            binding.photoViewButton.setPadding(16)
            Glide.with(binding.photoViewButton)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.photoViewButton)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateCameraUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpPinchToZoom() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scale = mCamera!!.cameraInfo.zoomState.value!!.zoomRatio * detector.scaleFactor
                mCamera!!.cameraControl.setZoomRatio(scale)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener)
        binding.viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    private val format = "'fn'_yyyyMMddHHmmss"

    private fun updateCameraUi() {
        lifecycleScope.launch(Dispatchers.IO) {
            val files = mOutputDirectory.listFiles { file ->
                EXTENSION_WHITELIST.contains(
                    file.extension.uppercase(
                        Locale.ROOT
                    )
                )
            }
            if (!files.isNullOrEmpty()) {
                val latestFile = files.maxByOrNull { it.lastModified() }
                latestFile?.let { setGalleryThumbnail(Uri.fromFile(it)) }
            }
        }
        binding.cameraCaptureButton.setOnClickListener {
            mImageCapture?.let { imageCapture ->
                val photoFile = createFile(mOutputDirectory, format, PHOTO_EXTENSION)
                val metadata = Metadata().apply {
                    isReversedHorizontal = mDefaultLen == CameraSelector.LENS_FACING_FRONT
                }
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata)
                    .build()
                imageCapture.takePicture(
                    outputOptions,
                    cameraExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e("CAMERA", "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d("CAMERA", "Photo capture succeeded: $savedUri")
                            activity?.intent?.data = savedUri
                            activity?.setResult(Activity.RESULT_OK, activity?.intent)
                            activity?.finish()
                            setGalleryThumbnail(savedUri)
                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(savedUri.toFile().extension)
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(savedUri.toString()),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                Log.d("CAMERA", "Image capture scanned into media store: $uri")
                            }
                        }
                    })
                mContainer.postDelayed(
                    {
                        mContainer.foreground = ColorDrawable(Color.WHITE)
                        mContainer.postDelayed(
                            { mContainer.foreground = null },
                            ANIMATION_FAST_MILLIS
                        )
                    },
                    ANIMATION_SLOW_MILLIS
                )
            }
        }
        binding.cameraSwitchButton.setOnClickListener {
            mDefaultLen = if (CameraSelector.LENS_FACING_FRONT == mDefaultLen) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            bindCameraUseCases()
        }

        binding.photoViewButton.setOnClickListener {
            if (true == mOutputDirectory.listFiles()?.isNotEmpty()) {
                Navigation.findNavController(
                    requireActivity(), R.id.fragment_container
                ).navigate(
                    CameraFragmentDirections.actionCameraToGallery(mOutputDirectory.absolutePath)
                )
            }
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun bindCameraUseCases() {
        val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = binding.viewFinder.display.rotation
        // Bind the CameraProvider to the LifeCycleOwner
        val cameraSelector = CameraSelector.Builder().requireLensFacing(mDefaultLen).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy(screenAspectRatio, FALLBACK_RULE_AUTO))
                .build()
            mCameraPreview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(rotation)
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            // ImageCapture
            mImageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(rotation)
                .build()
            // ImageAnalysis
            mImageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(rotation)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d("CAMERA", "Average luminosity: $luma")
                    })
                }
            cameraProvider.unbindAll()
            try {
                mCamera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    mCameraPreview,
                    mImageCapture,
                    mImageAnalyzer
                )
                setUpPinchToZoom()
            } catch (exc: Exception) {
                Log.e("CAMERA", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        /**
         * Used to add listeners that will be called with each luma computed
         */
        fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)

        /**
         * Helper extension function used to extract a byte array from an image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) {
                image.close()
                return
            }

            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)
            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0
            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases
            lastAnalyzedTimestamp = frameTimestamps.first
            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val buffer = image.planes[0].buffer
            // Extract image data from callback object
            val data = buffer.toByteArray()
            // Convert the data into an array of pixel values ranging 0-255
            val pixels = data.map { it.toInt() and 0xFF }
            // Compute average luminance for the image
            val luma = pixels.average()
            // Call all listeners with new value
            listeners.forEach { it(luma) }
            image.close()
        }
    }

}

