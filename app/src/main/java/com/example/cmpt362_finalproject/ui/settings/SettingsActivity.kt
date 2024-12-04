package com.example.cmpt362_finalproject.ui.settings

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_finalproject.databinding.ActivitySettingsBinding
import com.example.cmpt362_finalproject.data.UserPreferenceDatabase
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        
        // Initialize ViewModel
        val database = UserPreferenceDatabase.getInstance(this)
        val repository = UserPreferenceRepository(database.userPreferenceDao)
        val viewModelFactory = SettingsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        // Set current user email
        binding.emailTextView.text = FirebaseAuth.getInstance().currentUser?.email ?: "Not signed in"
        
        // Save button click listener
        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val monthlyLimit = binding.monthlyLimitEditText.text.toString().toDoubleOrNull()
            val savingsGoal = binding.savingsGoalEditText.text.toString().toDoubleOrNull()
            val spendingChallenge = binding.spendingChallengeEditText.text.toString().toDoubleOrNull()
            
            if (monthlyLimit != null && savingsGoal != null && spendingChallenge != null) {
                viewModel.updatePreferences(name, monthlyLimit, savingsGoal, spendingChallenge)
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.userPreferences.observe(this) { preferences ->
            preferences?.let {
                binding.nameEditText.setText(it.userName)
                binding.monthlyLimitEditText.setText(it.monthlyLimit.toString())
                binding.savingsGoalEditText.setText(it.savingsGoal.toString())
                binding.spendingChallengeEditText.setText(it.spendingChallenge.toString())
            } ?: run {
                binding.nameEditText.setText("")
                binding.monthlyLimitEditText.setText("0.0")
                binding.savingsGoalEditText.setText("0.0")
                binding.spendingChallengeEditText.setText("0.0")
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 