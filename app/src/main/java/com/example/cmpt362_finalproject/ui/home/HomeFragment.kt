package com.example.cmpt362_finalproject.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_finalproject.AddBillActivity
import com.example.cmpt362_finalproject.UpdateGoalChallengeActivity
import com.example.cmpt362_finalproject.data.BillModel
import com.example.cmpt362_finalproject.databinding.FragmentHomeBinding
import com.example.cmpt362_finalproject.ui.adapters.UpcomingBillsAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var billsAdapter: UpcomingBillsAdapter
    private val billsList = mutableListOf<BillModel>() // Manage bills here

    // Variables for storing goal and challenge values
    private var savingsGoal: String = "0.0" // Default value
    private var spendingChallenge: String = "0.0" // Default value

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up RecyclerView for Upcoming Bills
        billsAdapter = UpcomingBillsAdapter(billsList) { position ->
            removeBill(position) // Handle remove button click
        }
        binding.recyclerViewUpcomingBills.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUpcomingBills.adapter = billsAdapter

        // Add new bill when "+" button is clicked
        binding.buttonAddNewBill.setOnClickListener {
            val intent = Intent(requireContext(), AddBillActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_BILL)
        }

        // Update goal and challenge when "Update Goals & Challenges" button is clicked
        binding.buttonUpdateGoalChallenge.setOnClickListener {
            val intent = Intent(requireContext(), UpdateGoalChallengeActivity::class.java)
            intent.putExtra("current_savings_goal", savingsGoal)
            intent.putExtra("current_spending_challenge", spendingChallenge)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_GOAL_CHALLENGE)
        }

        // Update UI with initial values for goal and challenge
        updateGoalChallengeUI()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Handle results from AddBillActivity and UpdateGoalChallengeActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ADD_BILL -> {
                if (resultCode == Activity.RESULT_OK) {
                    val name = data?.getStringExtra("bill_name") ?: return
                    val dueDate = data.getStringExtra("due_date") ?: return
                    val amount = data.getStringExtra("amount") ?: return
                    addBill(BillModel(name, dueDate, amount)) // Add new bill
                }
            }
            REQUEST_CODE_UPDATE_GOAL_CHALLENGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val newSavingsGoal = data?.getStringExtra("new_savings_goal") ?: return
                    val newSpendingChallenge = data.getStringExtra("new_spending_challenge") ?: return
                    updateGoalChallenge(newSavingsGoal, newSpendingChallenge) // Update goal and challenge values
                }
            }
        }
    }

    // Add bill to the list and refresh the adapter
    private fun addBill(bill: BillModel) {
        billsList.add(bill)
        billsAdapter.notifyItemInserted(billsList.size - 1)
    }

    // Remove bill from the list and refresh the adapter
    private fun removeBill(position: Int) {
        billsList.removeAt(position)
        billsAdapter.notifyItemRemoved(position)
    }

    // Update goal and challenge values and UI
    private fun updateGoalChallenge(newSavingsGoal: String, newSpendingChallenge: String) {
        savingsGoal = newSavingsGoal
        spendingChallenge = newSpendingChallenge
        updateGoalChallengeUI()
    }

    // Update the UI to display current goal and challenge values
    private fun updateGoalChallengeUI() {
        binding.textViewSavingsGoal.text = "0 / $savingsGoal"
        binding.textViewSpendingChallenge.text = "0 / $spendingChallenge"
    }

    companion object {
        private const val REQUEST_CODE_ADD_BILL = 1
        private const val REQUEST_CODE_UPDATE_GOAL_CHALLENGE = 2
    }
}