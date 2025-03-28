package com.example.cmpt362_finalproject.ui.analytics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cmpt362_finalproject.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.lifecycle.ViewModelProvider
import android.graphics.Color
import com.example.cmpt362_finalproject.data.UserPreferenceDatabase
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.example.cmpt362_finalproject.manager.FirestoreManager
import com.example.cmpt362_finalproject.ui.transactions.PurchaseDatabase
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import com.github.mikephil.charting.components.LimitLine

class AnalyticsFragment : Fragment(R.layout.fragment_analytics) {
    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var chart: LineChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        chart = view.findViewById(R.id.chartSpendingTrends)
        setupChart()
        
        // Initialize database components
        val database = PurchaseDatabase.getInstance(requireActivity())
        val databaseDao = database.commentDatabaseDao
        val repository = PurchaseRepository(databaseDao, FirestoreManager())
        val userPrefDatabase = UserPreferenceDatabase.getInstance(requireActivity())
        val userPrefRepository = UserPreferenceRepository(userPrefDatabase.userPreferenceDao)
        
        // Create ViewModel using factory
        val viewModelFactory = AnalyticsViewModelFactory(repository, userPrefRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[AnalyticsViewModel::class.java]
        
        viewModel.spendingData.observe(viewLifecycleOwner) { (dailySpending, monthlyLimit) ->
            updateChartData(dailySpending, monthlyLimit)
        }
    }

    private fun setupChart() {
        chart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                granularity = 1f
                axisMinimum = 1f
                axisMaximum = 31f  // Show all days of the month
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }

            axisLeft.apply {
                setDrawGridLines(true)
                setDrawZeroLine(true)
                axisMinimum = 0f  // Start from 0
                spaceTop = 15f    // Add 15% space on top for better visibility
            }

            axisRight.isEnabled = false
            legend.apply {
                isEnabled = true
                textSize = 12f
                formSize = 12f
            }

            extraBottomOffset = 10f  // Add space at the bottom
            extraLeftOffset = 10f    // Add space on the left
        }
    }

    private fun updateChartData(dailySpending: Map<Int, Double>, monthlyLimit: Double) {
        val entries = dailySpending.map { (day, amount) ->
            Entry(day.toFloat(), amount.toFloat())
        }.sortedBy { it.x }

        val dataSet = LineDataSet(entries, "Daily Spending").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 3f
            circleRadius = 6f
            setDrawValues(true)
            valueTextSize = 10f
            mode = LineDataSet.Mode.LINEAR  // Connect points with straight lines
            setDrawFilled(true)
            fillColor = Color.BLUE
            fillAlpha = 30
        }

        // Add monthly limit line
        val limitLine = LimitLine(monthlyLimit.toFloat(), "Monthly Limit").apply {
            lineWidth = 3f
            lineColor = Color.RED
            enableDashedLine(10f, 10f, 0f)
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            textSize = 12f
            textColor = Color.RED
        }

        chart.axisLeft.apply {
            removeAllLimitLines()
            addLimitLine(limitLine)
            axisMaximum = monthlyLimit.toFloat() * 1.2f  // Set max to 120% of limit
        }

        chart.data = LineData(dataSet)
        chart.invalidate()
    }
} 