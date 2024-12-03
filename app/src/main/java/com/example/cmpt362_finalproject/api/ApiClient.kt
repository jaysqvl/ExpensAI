package com.example.cmpt362_finalproject.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    val visionRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(VISION_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit instance for Text Service
    val textRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(TEXT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
