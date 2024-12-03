package com.example.cmpt362_finalproject.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object ApiClient {
    // Base URLs for each service
    private const val VISION_BASE_URL = "https://us-west1-expensai-441100.cloudfunctions.net/"
    private const val TEXT_BASE_URL = "https://us-west1-expensai-441100.cloudfunctions.net/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    // Retrofit instance for Vision Service
    private val visionRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(VISION_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit instance for Text Service
    private val textRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(TEXT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Service instances
    val textService: TextService = textRetrofit.create(TextService::class.java)
    val visionService: VisionService = visionRetrofit.create(VisionService::class.java)
}
