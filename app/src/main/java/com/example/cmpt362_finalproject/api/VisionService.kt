package com.example.cmpt362_finalproject.api

import retrofit2.http.Body
import retrofit2.http.POST

interface VisionService {
    @POST("visionservice")
    suspend fun processImage(@Body request: VisionRequest): VisionResponse
    
    @POST("visionservice")
    suspend fun getVisionResponse(@Body request: VisionRequest): VisionResponse
}

data class VisionRequest(val image_data: String)
data class VisionResponse(val output: String)
