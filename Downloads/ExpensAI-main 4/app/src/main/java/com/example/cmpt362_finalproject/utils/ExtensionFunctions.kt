// ExtensionFunctions.kt
package com.example.cmpt362_finalproject.utils

import com.example.cmpt362_finalproject.data.BillEntity
import com.example.cmpt362_finalproject.data.BillModel

fun BillModel.toBillEntity(): BillEntity = BillEntity(
    id = this.id,
    title = this.title,
    amount = this.amount,
    dueDate = this.dueDate
)
