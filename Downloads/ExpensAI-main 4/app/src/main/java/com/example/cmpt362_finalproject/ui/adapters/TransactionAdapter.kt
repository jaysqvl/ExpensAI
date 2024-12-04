package com.example.cmpt362_finalproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.ui.transactions.Entry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(private var transactions: List<Entry>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    fun updateData(newTransactions: List<Entry>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.transactionTitle)
        private val amount: TextView = itemView.findViewById(R.id.transactionAmount)
        private val date: TextView = itemView.findViewById(R.id.transactionDate)

        fun bind(entry: Entry) {
            title.text = entry.storeName
            amount.text = "$${entry.paid / 100.0}" // Convert cents to dollars
            date.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(Date(entry.dateTime * 1000))
        }
    }
}