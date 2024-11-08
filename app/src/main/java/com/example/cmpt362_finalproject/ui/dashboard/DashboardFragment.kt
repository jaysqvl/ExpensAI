package com.example.cmpt362_finalproject.ui.dashboard

import android.widget.TextView
import com.example.cmpt362_finalproject.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_finalproject.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

data class RecentItem(
    val name: String,
    val date: String,
    val amount: String
)

class RecentItemAdapter(private val items: List<RecentItem>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<RecentItemAdapter.RecentItemViewHolder>() {

    class RecentItemViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent, parent, false)
        return RecentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentItemViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.dateTextView.text = item.date
        holder.amountTextView.text = item.amount
    }

    override fun getItemCount(): Int = items.size
}

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        setupPieChart()
        setupRecentItems()

        return binding.root
    }

    // This is the pie chart --> will attach documentation the library in discord

    private fun setupPieChart() {
        val entries = arrayListOf(
            PieEntry(37.5f, "Category 1"),
            PieEntry(20f, "Category 2"),
            PieEntry(15f, "Category 3"),
            PieEntry(12.5f, "Category 4"),
            PieEntry(9.8f, "Category 5"),
            PieEntry(5.6f, "Category 6")
        )

        val dataSet = PieDataSet(entries, "Expenses")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 16f

        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.setCenterTextSize(24f)
        binding.pieChart.invalidate() // Refresh the chart
    }

    private fun setupRecentItems() {
        // Hardcoded
        val recentItems = listOf(
            RecentItem("Spotify Subscription", "21 Jun 2022", "-9.99 $"),
            RecentItem("Amazon Purchase", "20 Jun 2022", "-29.99 $"),
            RecentItem("Salary", "19 Jun 2022", "+3000 $"),
            RecentItem("Uber Ride", "18 Jun 2022", "-7.50 $"),
            RecentItem("Grocery Store", "17 Jun 2022", "-45.00 $"),
            RecentItem("Gym Membership", "16 Jun 2022", "-19.99 $"),
            RecentItem("Coffee", "15 Jun 2022", "-4.50 $"),
            RecentItem("Electricity Bill", "14 Jun 2022", "-50.00 $"),
            RecentItem("Freelance Payment", "13 Jun 2022", "+500 $"),
            RecentItem("Restaurant", "12 Jun 2022", "-70.00 $")
        )

        // ADAPTER
        val adapter = RecentItemAdapter(recentItems)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

