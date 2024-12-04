package com.example.cmpt362_finalproject.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cmpt362_finalproject.R

class UpdateGoalChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_goal_challenge_activity)

        // Find views
        val amountSavedEditText = findViewById<EditText>(R.id.editTextAmountSaved)
        val savingsGoalEditText = findViewById<EditText>(R.id.editTextSavingsGoal)
        val spendingChallengeEditText = findViewById<EditText>(R.id.editTextSpendingChallenge)
        val saveButton = findViewById<Button>(R.id.buttonSaveGoalChallenge)

        // Pre-fill the fields if data is passed in
        val currentAmountSaved = intent.getStringExtra("currentAmountSaved") ?: ""
        val currentSavingsGoal = intent.getStringExtra("currentSavingsGoal") ?: ""
        val currentSpendingChallenge = intent.getStringExtra("currentSpendingChallenge") ?: ""
        amountSavedEditText.setText(currentAmountSaved)
        savingsGoalEditText.setText(currentSavingsGoal)
        spendingChallengeEditText.setText(currentSpendingChallenge)

        // Handle save button click
        saveButton.setOnClickListener {
            val newAmountSaved = amountSavedEditText.text.toString().trim()
            val newSavingsGoal = savingsGoalEditText.text.toString().trim()
            val newSpendingChallenge = spendingChallengeEditText.text.toString().trim()

            if (newAmountSaved.isBlank() || newSavingsGoal.isBlank() || newSpendingChallenge.isBlank()) {
                // Show a Toast if any field is empty
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Validate numeric inputs for savings fields
                try {
                    val amountSavedNumber = newAmountSaved.toDouble()
                    val savingsGoalNumber = newSavingsGoal.toDouble()

                    // Return the updated values to the calling activity
                    val resultIntent = Intent().apply {
                        putExtra("updatedAmountSaved", amountSavedNumber.toString())
                        putExtra("updatedSavingsGoal", savingsGoalNumber.toString())
                        putExtra("updatedSpendingChallenge", newSpendingChallenge)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish() // Close the activity
                } catch (e: NumberFormatException) {
                    // Show a Toast if input is not valid
                    Toast.makeText(this, "Please enter valid numbers for savings fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}