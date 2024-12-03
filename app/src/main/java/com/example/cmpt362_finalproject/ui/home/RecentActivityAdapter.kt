package com.example.cmpt362_finalproject.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.data.RecentActivity

class RecentActivityAdapter(
    private val recentActivities: List<RecentActivity>
) : RecyclerView.Adapter<RecentActivityAdapter.RecentActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return RecentActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentActivityViewHolder, position: Int) {
        val activity = recentActivities[position]
        holder.bind(activity)
    }

    override fun getItemCount() = recentActivities.size

    class RecentActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.activityTitle)
        private val amount: TextView = itemView.findViewById(R.id.activityAmount)
        private val date: TextView = itemView.findViewById(R.id.activityDate)

        fun bind(activity: RecentActivity) {
            title.text = activity.title
            amount.text = activity.amount
            date.text = activity.date
        }
    }
}