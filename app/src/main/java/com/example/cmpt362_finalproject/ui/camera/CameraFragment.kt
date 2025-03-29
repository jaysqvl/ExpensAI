package com.example.cmpt362_finalproject.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_finalproject.R
import java.io.ByteArrayOutputStream
import java.io.File
import com.example.cmpt362_finalproject.ui.transactions.PurchaseDatabase
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import com.example.cmpt362_finalproject.manager.FirestoreManager
import com.example.cmpt362_finalproject.api.ApiClient
import com.example.cmpt362_finalproject.manager.TransactionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import androidx.camera.view.PreviewView

// import textService

class CameraFragment : Fragment() {
    private lateinit var viewModel: CameraViewModel
    private var currentPhotoPath: String? = null
    private lateinit var viewFinder: PreviewView
    private var currentBitmap: Bitmap? = null
    private lateinit var submitButton: MaterialButton
    private lateinit var imagePreview: ImageView
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                processImage(uri)
            }
        }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                processImage(File(path))
            }
        }
    }

    @SuppressLint("CutPasteId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        
        // Initialize ViewModel first
        val database = PurchaseDatabase.getInstance(requireContext())
        val repository = PurchaseRepository(database.commentDatabaseDao, FirestoreManager())
        val transactionManager = TransactionManager(
            repository,
            ApiClient.textService,
            ApiClient.visionService
        )
        viewModel = ViewModelProvider(
            this,
            CameraViewModelFactory(transactionManager)
        )[CameraViewModel::class.java]
        
        viewFinder = view.findViewById(R.id.viewFinder)
        
        // Add an ImageView on top of the PreviewView to display selected images
        imagePreview = ImageView(requireContext())
        imagePreview.id = View.generateViewId()
        imagePreview.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imagePreview.scaleType = ImageView.ScaleType.FIT_CENTER
        imagePreview.visibility = View.GONE
        
        // Find the parent of the viewFinder and add the ImageView at the same position
        val parent = viewFinder.parent as ViewGroup
        parent.addView(imagePreview, parent.indexOfChild(viewFinder) + 1)
        
        val captureButton = view.findViewById<MaterialButton>(R.id.captureButton)
        captureButton.setOnClickListener {
            // Hide the image preview when starting camera
            imagePreview.visibility = View.GONE
            viewFinder.visibility = View.VISIBLE
            checkCameraPermission()
        }
        
        val fabGallery = view.findViewById<ExtendedFloatingActionButton>(R.id.fabGallery)
        fabGallery.setOnClickListener {
            openGallery()
        }

        // Add a submit button dynamically since it's not in the layout
        submitButton = MaterialButton(requireContext()).apply {
            text = "Submit Receipt"
            isEnabled = false
            id = View.generateViewId() // Generate an ID for the button
            setOnClickListener {
                Log.d("CameraFragment", "Submit button clicked")
                isEnabled = false  // Disable immediately after click
                
                currentBitmap?.let { bitmap ->
                    Log.d("CameraFragment", "Processing bitmap")
                    val compressedBitmap = compressBitmap(bitmap)
                    val base64Image = bitmapToBase64(compressedBitmap)
                    Log.d("CameraFragment", "Base64 string length: ${base64Image.length}")
                    viewModel.processReceipt(base64Image)
                    currentBitmap = null  // Clear the current bitmap
                    
                    // Don't reset the UI yet - wait for the response
                } ?: run {
                    Log.e("CameraFragment", "No image selected")
                    Toast.makeText(requireContext(), "Please select or capture an image first", Toast.LENGTH_SHORT).show()
                    isEnabled = true  // Re-enable only if no image was selected
                }
            }
        }

        // Find the first card view's linear layout to add the submit button
        val cardLayout = view.findViewById<ViewGroup>(R.id.viewFinder).parent as ViewGroup
        cardLayout.addView(submitButton)

        // Observe API response
        viewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            Log.d("CameraFragment", "API Response received: $response")
            Toast.makeText(requireContext(), "Receipt processed: $response", Toast.LENGTH_LONG).show()
            currentBitmap = null  // Clear the current image
            
            // Reset UI state
            resetUIState()
            
            submitButton.isEnabled = false  // Keep disabled after success
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Log.e("CameraFragment", "API Error: $error")
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            submitButton.isEnabled = true  // Re-enable on error
        }
        
        // Store the imagePreview reference in a property for access in processImage
        this.imagePreview = imagePreview
        
        return view
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show an explanation to the user
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required to take photos",
                    Toast.LENGTH_LONG
                ).show()
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                // No explanation needed, request the permission
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        // Reset UI state before launching camera
        imagePreview.visibility = View.GONE
        viewFinder.visibility = View.VISIBLE
        
        val photoFile = createImageFile()
        photoFile.also { file ->
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.cmpt362_finalproject.fileprovider",
                file
            )
            currentPhotoPath = file.absolutePath
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            cameraLauncher.launch(intent)
        }
    }

    private fun openGallery() {
        // Clear any existing image state
        currentBitmap = null
        submitButton.isEnabled = false
        
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        )
    }

    private fun processImage(source: Any) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = when (source) {
                    is Uri -> {
                        Log.d("CameraFragment", "Processing URI image")
                        withContext(Dispatchers.IO) {
                            val inputStream = requireContext().contentResolver.openInputStream(source)
                            BitmapFactory.decodeStream(inputStream)
                        }
                    }
                    is File -> {
                        Log.d("CameraFragment", "Processing File image")
                        withContext(Dispatchers.IO) {
                            val bitmap = BitmapFactory.decodeFile(source.absolutePath)
                            val matrix = Matrix()
                            matrix.postRotate(90f)
                            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                        }
                    }
                    else -> throw IllegalArgumentException("Unsupported image source")
                }

                withContext(Dispatchers.Main) {
                    currentBitmap = bitmap
                    
                    // Display the image in the ImageView
                    viewFinder.visibility = View.INVISIBLE
                    imagePreview.setImageBitmap(bitmap)
                    imagePreview.visibility = View.VISIBLE
                    
                    submitButton.isEnabled = true
                    Log.d("CameraFragment", "Image processed successfully and displayed")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CameraFragment", "Error processing image", e)
                    Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun compressBitmap(original: Bitmap): Bitmap {
        // Start with larger dimensions to maintain quality
        val maxDimension = 1200
        val ratio = Math.min(
            maxDimension.toFloat() / original.width,
            maxDimension.toFloat() / original.height
        )
        
        val width = (ratio * original.width).toInt()
        val height = (ratio * original.height).toInt()

        return Bitmap.createScaledBitmap(original, width, height, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var quality = 100
        var base64String: String
        
        do {
            byteArrayOutputStream.reset() // Clear the stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            quality -= 10 // Reduce quality by 10% each iteration
        } while (base64String.length > MAX_BASE64_SIZE && quality > 10)

        Log.d("CameraFragment", "Final image quality: $quality%, Size: ${base64String.length} chars")
        return base64String
    }

    private fun resetUIState() {
        // Reset ImageView and PreviewView state
        imagePreview.visibility = View.GONE
        imagePreview.setImageBitmap(null)
        viewFinder.visibility = View.VISIBLE
        
        // For the camera, we might need to restart the camera
        // This depends on how your camera is implemented
        // If using CameraX, you might need code to restart the camera preview
    }

    companion object {
        private const val MAX_BASE64_SIZE = 6800000 // ~5MB in base64 (5MB * 4/3)
    }
}