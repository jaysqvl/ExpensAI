package com.example.cmpt362_finalproject.data

data class Transaction(
    val title: String,     // Title or description of the transaction
    val amount: String,    // Transaction amount (e.g., "-$45.00")
    val date: String       // Transaction date (e.g., "25 Nov 2024")
)