package com.example.cmpt362_finalproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Observe ViewModel data
        homeViewModel.recentActivities.observe(viewLifecycleOwner) { activities ->
            binding.recyclerViewRecentActivity.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewRecentActivity.adapter = RecentActivityAdapter(activities)
        }

        homeViewModel.dailySpending.observe(viewLifecycleOwner) {
            binding.textViewDailySpending.text = it
        }

        homeViewModel.weeklySpending.observe(viewLifecycleOwner) {
            binding.textViewWeeklySpending.text = it
        }

        homeViewModel.savingsGoal.observe(viewLifecycleOwner) {
            binding.textViewSavingsGoal.text = it
        }

        homeViewModel.challenge.observe(viewLifecycleOwner) {
            binding.textViewChallenge.text = it
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}