package com.example.cmpt362_finalproject.ui.camera

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.api.ApiClient
import com.example.cmpt362_finalproject.api.VisionRequest
import com.example.cmpt362_finalproject.api.VisionService
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
    private val _apiResponse = MutableLiveData<String>()
    val apiResponse: LiveData<String> = _apiResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val visionService = ApiClient.visionRetrofit.create(VisionService::class.java)

    fun processReceipt(base64Image: String) {
        Log.d("CameraViewModel", "Processing receipt, base64 length: ${base64Image.length}")
        viewModelScope.launch {
            try {
                Log.d("CameraViewModel", "Making API request")
                val request = VisionRequest(image_data = base64Image)
                val response = visionService.processImage(request)
                Log.d("CameraViewModel", "API Response received: ${response.output}")
                _apiResponse.value = response.output
            } catch (e: Exception) {
                Log.e("CameraViewModel", "API Error", e)
                Log.e("CameraViewModel", "Error message: ${e.message}")
                Log.e("CameraViewModel", "Error cause: ${e.cause}")
                
                when (e) {
                    is retrofit2.HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        Log.e("CameraViewModel", "Error body: $errorBody")
                        _error.value = "Server error: $errorBody"
                    }
                    else -> {
                        _error.value = "Error processing receipt: ${e.message}"
                    }
                }
            }
        }
    }
}