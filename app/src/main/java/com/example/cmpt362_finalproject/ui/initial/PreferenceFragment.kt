package com.example.cmpt362_finalproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.data.UserPreferenceDatabase
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.example.cmpt362_finalproject.ui.initial.CategoriesFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PreferenceFragment : Fragment() {
    private lateinit var userPreferenceRepository: UserPreferenceRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_preference, container, false)
        
        // Initialize repository
        val database = UserPreferenceDatabase.getInstance(requireContext())
        userPreferenceRepository = UserPreferenceRepository(database.userPreferenceDao)
        
        val radioGroup = view.findViewById<RadioGroup>(R.id.saving_preferences_group)
        val fab = view.findViewById<FloatingActionButton>(R.id.confirm_preferences_fab)

        fab.setOnClickListener {
            val selectedPreference = when (radioGroup.checkedRadioButtonId) {
                R.id.preference_aggressive -> {
                    initializePreferences(2000.0, 5000.0, 1500.0) // Aggressive savings
                }
                R.id.preference_normal -> {
                    initializePreferences(1500.0, 3000.0, 1000.0) // Normal savings
                }
                R.id.preference_minimum -> {
                    initializePreferences(1000.0, 1000.0, 500.0) // Minimum savings
                }
                else -> null
            }

            if (selectedPreference != null) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CategoriesFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        return view
    }

    private fun initializePreferences(monthlyLimit: Double, savingsGoal: Double, spendingChallenge: Double) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        viewLifecycleOwner.lifecycleScope.launch {
            userPreferenceRepository.initializePreferences(
                monthlyLimit = monthlyLimit,
                savingsGoal = savingsGoal,
                spendingChallenge = spendingChallenge,
                email = currentUser?.email ?: "",
                userName = currentUser?.displayName ?: ""
            )
        }
    }
}
