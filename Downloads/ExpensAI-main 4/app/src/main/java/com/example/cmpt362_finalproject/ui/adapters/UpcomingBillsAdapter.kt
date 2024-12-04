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
    private val onRemoveClicked: (Int) -> Unit
) : RecyclerView.Adapter<UpcomingBillsAdapter.BillViewHolder>() {

    class BillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val billName: TextView = view.findViewById(R.id.billNameTextView)
        val billDetails: TextView = view.findViewById(R.id.billDetailsTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.buttonDeleteBill)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.billName.text = bill.title
        holder.billDetails.text = "Due: ${bill.dueDate} | Amount: $${bill.amount}"
        holder.deleteButton.setOnClickListener {
            onRemoveClicked(position)
        }
    }

    override fun getItemCount(): Int = bills.size

    // New method to retrieve a bill at a specific position
    fun getBillAt(position: Int): BillModel {
        return bills[position]
    }

    // New method to update the list of bills
    fun updateBills(newBills: List<BillModel>) {
        bills.clear()
        bills.addAll(newBills)
        notifyDataSetChanged()
    }
}