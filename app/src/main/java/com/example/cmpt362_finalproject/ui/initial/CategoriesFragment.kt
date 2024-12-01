package com.example.cmpt362_finalproject.ui.initial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cmpt362_finalproject.MainActivity
import com.example.cmpt362_finalproject.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesFragment : Fragment() {

    private val categoriesList = listOf(
        "Food",
        "Entertainment",
        "Travel",
        "Clothing",
        "Fitness",
        "Education",
        "Healthcare",
        "Savings"
    )
    private val selectedCategories = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_categories, container, false)

        val categoriesContainer: LinearLayout = rootView.findViewById(R.id.categories_container)

        categoriesList.forEach { category ->
            val checkBox = CheckBox(requireContext()).apply {
                text = category
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCategories.add(category)
                    } else {
                        selectedCategories.remove(category)
                    }
                }
            }
            categoriesContainer.addView(checkBox)
        }

        val floatingButton: FloatingActionButton = rootView.findViewById(R.id.floating_button)
        floatingButton.setOnClickListener {
            if (selectedCategories.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Selected: ${selectedCategories.joinToString(", ")}",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO:save
                savePreferencesCompleted()
                requireActivity().startActivity(
                    Intent(requireContext(), MainActivity::class.java)
                )
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Please select at least one category", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }
    private fun savePreferencesCompleted() {
        val sharedPreferences = requireActivity().getSharedPreferences("ExpensAI", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("preferences_completed", true).apply()
    }
}