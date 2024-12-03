package com.example.cmpt362_finalproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UpdateGoalChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_goal_challenge_activity)

        // Find views
        val savingsGoalEditText = findViewById<EditText>(R.id.editTextSavingsGoal)
        val spendingChallengeEditText = findViewById<EditText>(R.id.editTextSpendingChallenge)
        val saveButton = findViewById<Button>(R.id.buttonSaveGoalChallenge)

        // Pre-fill the fields if data is passed in
        val currentSavingsGoal = intent.getStringExtra("current_savings_goal")
        val currentSpendingChallenge = intent.getStringExtra("current_spending_challenge")
        savingsGoalEditText.setText(currentSavingsGoal)
        spendingChallengeEditText.setText(currentSpendingChallenge)

        // Handle save button click
        saveButton.setOnClickListener {
            val newSavingsGoal = savingsGoalEditText.text.toString()
            val newSpendingChallenge = spendingChallengeEditText.text.toString()

            if (newSavingsGoal.isBlank() || newSpendingChallenge.isBlank()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                val resultIntent = intent
                resultIntent.putExtra("new_savings_goal", newSavingsGoal)
                resultIntent.putExtra("new_spending_challenge", newSpendingChallenge)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}