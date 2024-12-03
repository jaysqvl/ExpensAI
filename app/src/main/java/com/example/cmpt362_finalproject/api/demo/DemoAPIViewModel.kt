package com.example.cmpt362_finalproject.api.demo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.api.ApiClient
import com.example.cmpt362_finalproject.api.TextRequest
import com.example.cmpt362_finalproject.api.TextResponse
import com.example.cmpt362_finalproject.api.VisionRequest
import com.example.cmpt362_finalproject.api.VisionResponse
import kotlinx.coroutines.launch

class DemoAPIViewModel : ViewModel() {

    // LiveData for TextService responses
    private val _textResponse = MutableLiveData<TextResponse>()
    val textResponse: LiveData<TextResponse> = _textResponse

    // LiveData for VisionService responses
    private val _visionResponse = MutableLiveData<VisionResponse>()
    val visionResponse: LiveData<VisionResponse> = _visionResponse

    // LiveData for API error messages
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Initialize services using Retrofit
    private val textService = ApiClient.textRetrofit.create(com.example.cmpt362_finalproject.api.TextService::class.java)
    private val visionService = ApiClient.visionRetrofit.create(com.example.cmpt362_finalproject.api.VisionService::class.java)

    /**
     * Shows you how to call the TextService to process text data in a viewmodel
     * @param endpoint The endpoint type (e.g., "summary" or "insight").
     * @param inputText The input data for the API.
     */
    fun fetchTextResponse(endpoint: String, inputText: String) {
        viewModelScope.launch {
            try {
                val request = TextRequest(endpoint = endpoint, input = inputText)

                val response = textService.getTextResponse(request)

                _textResponse.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Failed to fetch text response: ${e.message}")
            }
        }
    }

    /**
     * Shows you how to call the VisionService to process an image in a viewmodel.
     * @param base64Image The Base64-encoded image data.
     */
    fun fetchVisionResponse(base64Image: String) {
        viewModelScope.launch {
            try {
                val request = VisionRequest(image_data = base64Image)
                val response = visionService.getVisionResponse(request)
                _visionResponse.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Failed to fetch vision response: ${e.message}")
            }
        }
    }
}
