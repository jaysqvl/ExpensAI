package com.example.cmpt362_finalproject.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionData(
    val merchant_name: String,
    val total_amount: Double
)
