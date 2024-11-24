package com.example.cmpt362_finalproject.ui.transactions

data class Purchase(
    val purchaseName: String,
    val storeName: String,
    val amount: Double,
    val date: Long
)