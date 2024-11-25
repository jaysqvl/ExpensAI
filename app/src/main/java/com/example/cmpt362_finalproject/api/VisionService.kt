package com.example.cmpt362_finalproject.api

import retrofit2.http.Body
import retrofit2.http.POST

interface VisionService {
    @POST("visionservice") // Only the endpoint path here
    suspend fun getVisionResponse(@Body request: VisionRequest): VisionResponse
}

data class VisionRequest(val image_data: String)
data class VisionResponse(val output: String)
