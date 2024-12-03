package com.example.cmpt362_finalproject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.data.BillModel

class UpcomingBillsAdapter(
    private val bills: MutableList<BillModel>,
    private val onRemoveClicked: (Int) -> Unit // Callback for delete button
) : RecyclerView.Adapter<UpcomingBillsAdapter.BillViewHolder>() {

    class BillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val billName: TextView = view.findViewById(R.id.billNameTextView)
        val billDetails: TextView = view.findViewById(R.id.billDetailsTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.buttonDeleteBill)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bill_item_layout, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.billName.text = bill.name
        holder.billDetails.text = "Due: ${bill.dueDate} | Amount: ${bill.amount}"
        holder.deleteButton.setOnClickListener {
            onRemoveClicked(position) // Trigger callback to remove the bill
        }
    }

    override fun getItemCount(): Int = bills.size
}