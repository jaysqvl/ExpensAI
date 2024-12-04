package com.example.cmpt362_finalproject.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_finalproject.databinding.ItemBillBinding
import com.example.cmpt362_finalproject.data.BillModel

class BillAdapter(
    private var items: List<BillModel> = emptyList(),
    private val onDeleteClick: (BillModel) -> Unit
) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    fun submitList(newItems: List<BillModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class BillViewHolder(private val binding: ItemBillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bill: BillModel) {
            binding.title.text = bill.title
            binding.amount.text = bill.amount.toString()
            binding.dueDate.text = bill.dueDate
            binding.deleteButton.setOnClickListener { onDeleteClick(bill) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = ItemBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}