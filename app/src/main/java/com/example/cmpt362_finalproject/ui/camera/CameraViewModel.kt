package com.example.cmpt362_finalproject.ui.camera

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.manager.TransactionManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class CameraViewModel @Inject constructor(
    private val transactionManager: TransactionManager
) : ViewModel() {
    private val _apiResponse = MutableLiveData<String>()
    val apiResponse: LiveData<String> = _apiResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun processReceipt(base64Image: String) {
        viewModelScope.launch {
            try {
                Log.d("CameraViewModel", "Starting receipt processing")
                val result = transactionManager.processReceipt(base64Image)
                result.fold(
                    onSuccess = { entry ->
                        _apiResponse.value = "Transaction added: ${entry.storeName}"
                    },
                    onFailure = { e ->
                        Log.e("CameraViewModel", "Error details: ", e)
                        _error.value = "Error processing receipt: ${e.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Exception details: ", e)
                _error.value = "Error processing receipt: ${e.message}"
            }
        }
    }
}