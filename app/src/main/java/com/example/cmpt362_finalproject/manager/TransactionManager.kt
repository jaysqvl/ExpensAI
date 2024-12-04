package com.example.cmpt362_finalproject.manager

import com.example.cmpt362_finalproject.api.*
import com.example.cmpt362_finalproject.model.TransactionData
import com.example.cmpt362_finalproject.ui.transactions.Entry
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import javax.inject.Inject
import kotlinx.serialization.json.Json
import android.util.Log

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
                    val errorResponse = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                    }.decodeFromString<ErrorResponse>(errorBody ?: "{}")
                    Log.d("TransactionManager", "Parsed error response: $errorResponse")
                    errorResponse.output?.error ?: errorResponse.error ?: "Unknown error"
                } catch (e: Exception) {
                    Log.e("TransactionManager", "Error parsing error response", e)
                    "Server error: ${response.code()} - ${response.message()}"
                }
                
                return Result.failure(Exception(errorMessage))
            }
            
            val visionResponse = response.body()
            if (visionResponse?.output == null) {
                Log.e("TransactionManager", "Null response from server")
                return Result.failure(Exception("Received null response from server"))
            }
            
            try {
                Log.d("TransactionManager", "Attempting to parse response output: ${visionResponse.output}")
                Log.d("TransactionManager", "Raw vision response: ${visionResponse.output}")
                val transactionData = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }.decodeFromString<TransactionData>(visionResponse.output)
                Log.d("TransactionManager", "Parsed TransactionData: $transactionData")
                
                val entry = Entry(
                    storeName = transactionData.merchant_name,
                    paid = (transactionData.total_amount * 100).toInt(),
                    dateTime = System.currentTimeMillis() / 1000
                )
                Log.d("TransactionManager", "Created Entry: $entry")
                
                purchaseRepository.insert(entry)
                Result.success(entry)
            } catch (e: Exception) {
                Log.e("TransactionManager", "Error parsing transaction data", e)
                Result.failure(Exception("Failed to parse receipt data: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error in processReceipt", e)
            Result.failure(e)
        }
    }
}