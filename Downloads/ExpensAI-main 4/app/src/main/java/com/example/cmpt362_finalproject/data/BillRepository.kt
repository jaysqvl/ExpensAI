package com.example.cmpt362_finalproject.data

class BillRepository(private val billDao: BillDao) {

    fun getAllBills() = billDao.getAllBills()

    suspend fun insertBill(bill: BillEntity) {
        billDao.insertBill(bill)
    }

    suspend fun deleteBill(bill: BillEntity) {
        billDao.deleteBill(bill)
    }
}