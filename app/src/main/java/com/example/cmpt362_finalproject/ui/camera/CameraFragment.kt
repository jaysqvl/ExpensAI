package com.example.cmpt362_finalproject.ui.camera

import android.Manifest
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
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cmpt362_finalproject.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CameraFragment : Fragment() {
    private val viewModel: CameraViewModel by viewModels()
    private var currentPhotoPath: String? = null
    private lateinit var receiptImageView: ImageView
    private var currentBitmap: Bitmap? = null
    
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        
        receiptImageView = view.findViewById(R.id.receiptImageView)
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        
        view.findViewById<View>(R.id.captureButton).setOnClickListener {
            checkCameraPermission()
        }
        
        view.findViewById<View>(R.id.galleryButton).setOnClickListener {
            openGallery()
        }

        submitButton.setOnClickListener {
            Log.d("CameraFragment", "Submit button clicked")
            currentBitmap?.let { bitmap ->
                Log.d("CameraFragment", "Processing bitmap")
                val compressedBitmap = compressBitmap(bitmap)
                val base64Image = bitmapToBase64(compressedBitmap)
                Log.d("CameraFragment", "Base64 string length: ${base64Image.length}")
                viewModel.processReceipt(base64Image)
            } ?: run {
                Log.e("CameraFragment", "No image selected")
                Toast.makeText(requireContext(), "Please select or capture an image first", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe API response
        viewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            Log.d("CameraFragment", "API Response received: $response")
            Toast.makeText(requireContext(), "Receipt processed: $response", Toast.LENGTH_LONG).show()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Log.e("CameraFragment", "API Error: $error")
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
        
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
        try {
            val bitmap = when (source) {
                is Uri -> {
                    Log.d("CameraFragment", "Processing URI image")
                    val inputStream = requireContext().contentResolver.openInputStream(source)
                    BitmapFactory.decodeStream(inputStream)
                }
                is File -> {
                    Log.d("CameraFragment", "Processing File image")
                    val bitmap = BitmapFactory.decodeFile(source.absolutePath)
                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                }
                else -> throw IllegalArgumentException("Unsupported image source")
            }

            currentBitmap = bitmap
            receiptImageView.setImageBitmap(bitmap)
            view?.findViewById<Button>(R.id.submitButton)?.isEnabled = true
            Log.d("CameraFragment", "Image processed successfully")
        } catch (e: Exception) {
            Log.e("CameraFragment", "Error processing image", e)
            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compressBitmap(original: Bitmap): Bitmap {
        // Calculate new dimensions while maintaining aspect ratio
        val maxDimension = 800
        val ratio = Math.min(
            maxDimension.toFloat() / original.width,
            maxDimension.toFloat() / original.height
        )
        
        val width = (ratio * original.width).toInt()
        val height = (ratio * original.height).toInt()

        val compressed = Bitmap.createScaledBitmap(original, width, height, true)
        
        // Display the image in the ImageView
        receiptImageView.setImageBitmap(compressed)
        
        return compressed
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}