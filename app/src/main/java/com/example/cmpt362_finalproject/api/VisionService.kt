package com.example.cmpt362_finalproject.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VisionService {
    @POST("visionservice")
    suspend fun getVisionResponse(@Body request: VisionRequest): Response<VisionResponse>
}
