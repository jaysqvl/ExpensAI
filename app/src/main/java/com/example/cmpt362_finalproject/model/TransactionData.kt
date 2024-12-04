package com.example.cmpt362_finalproject.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionData(
    val merchant_name: String,
    val items: List<ItemData>? = null,
    val tax: Double? = null,
    val total_amount: Double
)

@Serializable
data class ItemData(
    val name: String,
    val quantity: Int,
    val price_per_unit: Double,
    val total_price: Double
)
