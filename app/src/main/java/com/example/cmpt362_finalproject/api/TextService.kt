package com.example.cmpt362_finalproject.api

import retrofit2.http.Body
import retrofit2.http.POST

interface TextService {
    @POST("textservice") // Only the endpoint path here
    suspend fun getTextResponse(@Body request: TextRequest): TextResponse
}

data class TextRequest(val endpoint: String, val input: String)
data class TextResponse(val output: String)
