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
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import com.google.android.material.card.MaterialCardView
import com.google.android.material.radiobutton.MaterialRadioButton
import androidx.coordinatorlayout.widget.CoordinatorLayout

class PreferenceFragment : Fragment() {
    private lateinit var userPreferenceRepository: UserPreferenceRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Create the base layout
        val rootView = CoordinatorLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(resources.getColor(R.color.background_light, null))
        }
        
        // Initialize repository
        val database = UserPreferenceDatabase.getInstance(requireContext())
        userPreferenceRepository = UserPreferenceRepository(database.userPreferenceDao)
        
        // Create a new card for the savings preferences
        val card = MaterialCardView(requireContext()).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 32, 32, 32)
            }
            elevation = 4f
            radius = 16f
            setCardBackgroundColor(Color.WHITE)
        }
        
        // Create a container for the card content
        val cardContent = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(32, 32, 32, 32)
        }
        
        // Add title
        val title = TextView(requireContext()).apply {
            text = "How would you like to approach your savings goal?"
            textSize = 20f
            setTextColor(Color.BLACK)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 32)
            }
        }
        cardContent.addView(title)
        
        // Create radio group
        val radioGroup = RadioGroup(requireContext()).apply {
            id = View.generateViewId()
            orientation = RadioGroup.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        // Add aggressive option
        val aggressiveButton = MaterialRadioButton(requireContext()).apply {
            id = View.generateViewId()
            text = "Aggressive"
            textSize = 16f
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 8)
            }
        }
        radioGroup.addView(aggressiveButton)
        
        val aggressiveDesc = TextView(requireContext()).apply {
            text = "Monthly Limit: $2,000\nSavings Goal: $5,000\nSpending Challenge: $1,500\n\nMaximize savings to reach your goal as fast as possible."
            textSize = 14f
            setTextColor(Color.GRAY)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(64, 0, 0, 24)
            }
        }
        radioGroup.addView(aggressiveDesc)
        
        // Add normal option
        val normalButton = MaterialRadioButton(requireContext()).apply {
            id = View.generateViewId()
            text = "Normal"
            textSize = 16f
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 8)
            }
        }
        radioGroup.addView(normalButton)
        
        val normalDesc = TextView(requireContext()).apply {
            text = "Monthly Limit: $1,500\nSavings Goal: $3,000\nSpending Challenge: $1,000\n\nSave moderately, aiming for steady progress."
            textSize = 14f
            setTextColor(Color.GRAY)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(64, 0, 0, 24)
            }
        }
        radioGroup.addView(normalDesc)
        
        // Add minimum option
        val minimumButton = MaterialRadioButton(requireContext()).apply {
            id = View.generateViewId()
            text = "Minimum"
            textSize = 16f
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 8)
            }
        }
        radioGroup.addView(minimumButton)
        
        val minimumDesc = TextView(requireContext()).apply {
            text = "Monthly Limit: $1,000\nSavings Goal: $1,000\nSpending Challenge: $500\n\nFocus on meeting your goal with minimal savings pressure."
            textSize = 14f
            setTextColor(Color.GRAY)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(64, 0, 0, 24)
            }
        }
        radioGroup.addView(minimumDesc)
        
        // Add note
        val note = TextView(requireContext()).apply {
            text = "Note: These values can be customized anytime in the Settings."
            textSize = 14f
            setTextColor(Color.GRAY)
            setTypeface(null, android.graphics.Typeface.ITALIC)
            gravity = android.view.Gravity.CENTER
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 32, 0, 0)
            }
        }
        
        // Add all views to card
        cardContent.addView(radioGroup)
        cardContent.addView(note)
        card.addView(cardContent)
        
        // Add card to main container
        rootView.addView(card)
        
        // Add FAB
        val fab = FloatingActionButton(requireContext()).apply {
            id = View.generateViewId()
            setImageResource(android.R.drawable.ic_menu_send)
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
                setMargins(0, 0, 32, 32)
            }
        }
        rootView.addView(fab)
        
        fab.setOnClickListener {
            val selectedPreference = when (radioGroup.checkedRadioButtonId) {
                aggressiveButton.id -> {
                    initializePreferences(2000.0, 5000.0, 1500.0) // Aggressive savings
                }
                normalButton.id -> {
                    initializePreferences(1500.0, 3000.0, 1000.0) // Normal savings
                }
                minimumButton.id -> {
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
        return rootView
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
