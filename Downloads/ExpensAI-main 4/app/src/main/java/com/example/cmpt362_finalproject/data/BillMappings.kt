package com.example.cmpt362_finalproject.data

fun BillEntity.toBillModel(): BillModel = BillModel(
    id = this.id,
    title = this.title, // Replace 'name' with 'title' if that's the correct field
    amount = this.amount,
    dueDate = this.dueDate
)

