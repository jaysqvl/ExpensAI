package com.example.cmpt362_finalproject.ui.transactions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "purchase_table")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "cost_column")
    var paid: Int = 0,

    @ColumnInfo(name = "store_name_column")
    var storeName: String = "",

    @ColumnInfo(name = "date_time")
    var dateTime: Long = 0L,

)
