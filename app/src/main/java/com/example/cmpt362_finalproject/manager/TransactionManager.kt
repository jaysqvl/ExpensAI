package com.example.cmpt362_finalproject.manager

import com.example.cmpt362_finalproject.api.VisionRequest
import com.example.cmpt362_finalproject.api.TextRequest
import com.example.cmpt362_finalproject.api.TextService
import com.example.cmpt362_finalproject.api.VisionService
import com.example.cmpt362_finalproject.model.TransactionData
import com.example.cmpt362_finalproject.ui.transactions.Entry
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.util.Date

class TransactionManager @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val textService: TextService,
    private val visionService: VisionService
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    
    suspend fun processReceipt(imageBase64: String): Result<Entry> {
        return try {
            val response = visionService.getVisionResponse(VisionRequest(imageBase64))
            val transactionData = Json.decodeFromString<TransactionData>(response.output)
            
            val entry = Entry(
                storeName = transactionData.merchant_name,
                paid = (transactionData.total_amount * 100).toInt(), // Convert to cents
                dateTime = System.currentTimeMillis() / 1000
            )
            
            purchaseRepository.insert(entry)
            checkForInsightGeneration()
            
            Result.success(entry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun checkForInsightGeneration() {
        val transactions = purchaseRepository.allPurchases.first()
        if (transactions.size == 1 || transactions.size % 5 == 0) {
            generateInsights()
        }
    }
    
    private suspend fun generateInsights() {
        val transactions = purchaseRepository.allPurchases.first()
        val transactionData = transactions.joinToString("\n") { 
            "${it.storeName}: $${it.paid/100.0} on ${Date(it.dateTime * 1000)}"
        }
        
        textService.getTextResponse(TextRequest("summary", transactionData))
    }
}