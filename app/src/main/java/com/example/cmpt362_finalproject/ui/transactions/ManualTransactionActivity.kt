package com.example.cmpt362_finalproject.ui.transactions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.manager.FirestoreManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ManualTransactionActivity : AppCompatActivity() {
    
    private lateinit var storeNameEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    
    private lateinit var viewModel: PurchaseViewModel
    private var selectedTimestamp: Long = System.currentTimeMillis() / 1000
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_transaction)
        
        // Initialize database components
        val database = PurchaseDatabase.getInstance(this)
        val databaseDao = database.commentDatabaseDao
        val repository = PurchaseRepository(databaseDao, FirestoreManager())
        
        val viewModelFactory = PurchaseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[PurchaseViewModel::class.java]
        
        // Initialize UI components
        storeNameEditText = findViewById(R.id.editTextStoreName)
        amountEditText = findViewById(R.id.editTextAmount)
        dateEditText = findViewById(R.id.editTextDate)
        saveButton = findViewById(R.id.buttonSave)
        cancelButton = findViewById(R.id.buttonCancel)
        
        // Set current date
        updateDateDisplay()
        
        // Set up date picker
        dateEditText.setOnClickListener {
            showDatePicker()
        }
        
        // Set up save button
        saveButton.setOnClickListener {
            saveTransaction()
        }
        
        // Set up cancel button
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        
        // Observe for errors
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null && errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(selectedTimestamp * 1000) // Convert to milliseconds
            .build()
            
        datePicker.addOnPositiveButtonClickListener { timeInMillis ->
            selectedTimestamp = timeInMillis / 1000 // Store in seconds
            updateDateDisplay()
        }
        
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }
    
    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        dateEditText.setText(sdf.format(Date(selectedTimestamp * 1000)))
    }
    
    private fun saveTransaction() {
        val storeName = storeNameEditText.text.toString().trim()
        val amountText = amountEditText.text.toString().trim()
        
        if (storeName.isEmpty()) {
            storeNameEditText.error = "Please enter a merchant name"
            return
        }
        
        if (amountText.isEmpty()) {
            amountEditText.error = "Please enter an amount"
            return
        }
        
        try {
            // Convert dollar amount to cents (integer)
            val amountInDollars = amountText.toDouble()
            val amountInCents = (amountInDollars * 100).toInt()
            
            val entry = Entry(
                storeName = storeName,
                paid = amountInCents,
                dateTime = selectedTimestamp
            )
            
            viewModel.insert(entry)
            
            setResult(Activity.RESULT_OK)
            finish()
            
        } catch (e: NumberFormatException) {
            amountEditText.error = "Please enter a valid amount"
        }
    }
} 