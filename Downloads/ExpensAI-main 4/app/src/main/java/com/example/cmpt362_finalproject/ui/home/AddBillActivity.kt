package com.example.cmpt362_finalproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddBillActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_bill_activity)

        // Find views
        val billNameEditText = findViewById<EditText>(R.id.editTextBillName)
        val dueDateEditText = findViewById<EditText>(R.id.editTextDueDate)
        val amountEditText = findViewById<EditText>(R.id.editTextAmount)
        val saveButton = findViewById<Button>(R.id.buttonSaveBill)

        // Handle save button click
        saveButton.setOnClickListener {
            val billName = billNameEditText.text.toString()
            val dueDate = dueDateEditText.text.toString()
            val amount = amountEditText.text.toString()

            if (billName.isBlank() || dueDate.isBlank() || amount.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val resultIntent = intent
                resultIntent.putExtra("bill_name", billName)
                resultIntent.putExtra("due_date", dueDate)
                resultIntent.putExtra("amount", amount)
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
            }
        }
    }
}