package com.example.cmpt362_finalproject.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VisionService {
    @POST("visionservice")
    suspend fun getVisionResponse(@Body request: VisionRequest): Response<VisionResponse>
}

data class VisionRequest(val image_data: String)
data class VisionResponse(val output: String)
data class ErrorResponse(val error: String, val details: String? = null)
