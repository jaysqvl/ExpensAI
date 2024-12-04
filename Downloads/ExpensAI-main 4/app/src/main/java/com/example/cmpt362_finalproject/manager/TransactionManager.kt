package com.example.cmpt362_finalproject.manager

import com.example.cmpt362_finalproject.api.VisionRequest
import com.example.cmpt362_finalproject.api.TextService
import com.example.cmpt362_finalproject.api.VisionService
import com.example.cmpt362_finalproject.model.TransactionData
import com.example.cmpt362_finalproject.ui.transactions.Entry
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.serialization.json.Json
import android.util.Log

class TransactionManager @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val textService: TextService,
    private val visionService: VisionService
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    
    suspend fun processReceipt(imageBase64: String): Result<Entry> {
        return try {
            Log.d("TransactionManager", "Making vision API call with image length: ${imageBase64.length}")
            val response = visionService.getVisionResponse(VisionRequest(imageBase64))
            Log.d("TransactionManager", "Received response: ${response.output}")
            
            if (response.output == null) {
                return Result.failure(Exception("Received null response from server"))
            }
            
            if (response.output.contains("error")) {
                val errorJson = Json.decodeFromString<Map<String, Any>>(response.output)
                val logs = (errorJson["logs"] as? List<*>)?.joinToString("\n")
                Log.e("TransactionManager", "Server logs:\n$logs")
                return Result.failure(Exception(errorJson["error"] as String))
            }
            
            val transactionData = Json.decodeFromString<TransactionData>(response.output)
            val entry = Entry(
                storeName = transactionData.merchant_name,
                paid = (transactionData.total_amount * 100).toInt(), // Convert to cents
                dateTime = System.currentTimeMillis() / 1000
            )
            
            purchaseRepository.insert(entry)
            Result.success(entry)
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error in processReceipt", e)
            Result.failure(e)
        }
    }
}