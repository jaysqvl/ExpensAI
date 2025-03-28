package com.example.cmpt362_finalproject.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_finalproject.databinding.ActivitySettingsBinding
import com.example.cmpt362_finalproject.data.UserPreferenceDatabase
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.google.firebase.auth.FirebaseAuth
import com.example.cmpt362_finalproject.InitialActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
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
        binding.emailTextView.text = auth.currentUser?.email ?: "Not signed in"
        
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
        
        // Sign out button click listener
        binding.buttonSignOut.setOnClickListener {
            signOut()
        }
        
        // Sign out FAB click listener
        binding.fabLogout.setOnClickListener {
            signOut()
        }
    }
    
    private fun signOut() {
        auth.signOut()
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
        
        // Navigate to initial activity
        val intent = Intent(this, InitialActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
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