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
import com.example.cmpt362_finalproject.api.ErrorResponse

@Suppress("SENSELESS_COMPARISON", "unused")
class TransactionManager @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val textService: TextService,
    private val visionService: VisionService
) {

    suspend fun processReceipt(imageBase64: String): Result<Entry> {
        return try {
            if (imageBase64.isEmpty()) {
                return Result.failure(Exception("Empty image data"))
            }
            
            val response = visionService.getVisionResponse(VisionRequest(imageBase64))
            
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("TransactionManager", "Error response body: $errorBody")
                
                val errorMessage = try {
                    Json.decodeFromString<ErrorResponse>(errorBody ?: "").error
                } catch (e: Exception) {
                    "Server error: ${response.code()} - ${response.message()}"
                }
                
                return Result.failure(Exception(errorMessage))
            }
            
            val visionResponse = response.body()
            if (visionResponse?.output == null) {
                Log.e("TransactionManager", "Null response from server")
                return Result.failure(Exception("Received null response from server"))
            }
            
            val transactionData = Json.decodeFromString<TransactionData>(visionResponse.output)
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