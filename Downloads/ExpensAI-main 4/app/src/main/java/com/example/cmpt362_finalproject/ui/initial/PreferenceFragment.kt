package com.example.cmpt362_finalproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.ui.initial.CategoriesFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PreferenceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_preference, container, false)
        val radioGroup = view.findViewById<RadioGroup>(R.id.saving_preferences_group)
        val fab = view.findViewById<FloatingActionButton>(R.id.confirm_preferences_fab)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        fab.setOnClickListener {
            val selectedPreference = when (radioGroup.checkedRadioButtonId) {
                R.id.preference_aggressive -> "Aggressive"
                R.id.preference_normal -> "Normal"
                R.id.preference_minimum -> "Minimum"
                else -> null
            }

            selectedPreference?.let {
                sharedPreferences.edit().putString("saving_preference", it).apply()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CategoriesFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        return view
    }

}
