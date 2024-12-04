package com.example.cmpt362_finalproject.api

import kotlinx.serialization.Serializable

@Serializable
data class VisionRequest(val image_data: String)

@Serializable
data class VisionResponse(val output: String)

@Serializable
data class ErrorResponse(
    val error: String? = null,
    val details: String? = null,
    val output: ErrorOutput? = null
)

@Serializable
data class ErrorOutput(
    val error: String
) 