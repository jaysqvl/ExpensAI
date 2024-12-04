package com.example.cmpt362_finalproject.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.manager.FirestoreManager
import com.example.cmpt362_finalproject.ui.transactions.PurchaseDatabase
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.LimitLine
import java.util.Calendar

class AnalyticsFragment : Fragment() {
    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lineChart = view.findViewById(R.id.monthlySpendingChart)
        setupChart()
        
        // Initialize database components
        val database = PurchaseDatabase.getInstance(requireActivity())
        val repository = PurchaseRepository(database.commentDatabaseDao, FirestoreManager())
        val viewModelFactory = AnalyticsViewModelFactory(repository)
        
        viewModel = ViewModelProvider(this, viewModelFactory)[AnalyticsViewModel::class.java]
        
        viewModel.allPurchasesLiveData.observe(viewLifecycleOwner) {
            updateChart()
        }
    }

    private fun setupChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                // Set the minimum and maximum values for the X-axis
                val calendar = Calendar.getInstance()
                val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                axisMinimum = 1f
                axisMaximum = daysInMonth.toFloat()
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$${value}"
                    }
                }
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = true

            // Add limit line
            val limitLine = LimitLine(1000f, "Monthly Limit").apply {
                lineWidth = 2f
                lineColor = Color.RED
                textColor = Color.RED
                textSize = 12f
            }
            axisLeft.addLimitLine(limitLine)
        }
    }

    private fun updateChart() {
        val calendar = Calendar.getInstance()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dailySpending = viewModel.getDailySpending()
        
        val entries = (1..daysInMonth).map { day ->
            Entry(day.toFloat(), dailySpending[day]?.toFloat() ?: 0f)
        }

        val dataSet = LineDataSet(entries, "Daily Spending").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
        }

        lineChart.data = LineData(dataSet)
        lineChart.invalidate()
    }
} 