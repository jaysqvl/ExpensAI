package com.example.cmpt362_finalproject.manager

import com.example.cmpt362_finalproject.api.VisionRequest
import com.example.cmpt362_finalproject.api.TextService
import com.example.cmpt362_finalproject.api.VisionService
import com.example.cmpt362_finalproject.model.TransactionData
import com.example.cmpt362_finalproject.ui.transactions.Entry
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import javax.inject.Inject
import kotlinx.serialization.json.Json
import android.util.Log

@Suppress("SENSELESS_COMPARISON")
class TransactionManager @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val textService: TextService,
    private val visionService: VisionService
) {

    suspend fun processReceipt(imageBase64: String): Result<Entry> {
        return try {
            // Validate base64 string
            if (imageBase64.isEmpty()) {
                return Result.failure(Exception("Empty image data"))
            }
            
            // Log the first and last 50 characters of base64 string
            Log.d("TransactionManager", "Base64 prefix: ${imageBase64.take(50)}...")
            Log.d("TransactionManager", "Base64 suffix: ...${imageBase64.takeLast(50)}")
            
            val response = visionService.getVisionResponse(VisionRequest(imageBase64))
            
            if (response.output == null) {
                Log.e("TransactionManager", "Null response from server")
                return Result.failure(Exception("Received null response from server"))
            }
            
            // Log the full response for debugging
            Log.d("TransactionManager", "Full response: ${response.output}")
            
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