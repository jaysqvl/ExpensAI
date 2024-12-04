package com.example.cmpt362_finalproject.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmpt362_finalproject.AddBillActivity
import com.example.cmpt362_finalproject.data.BillModel
import com.example.cmpt362_finalproject.databinding.FragmentHomeBinding
import com.example.cmpt362_finalproject.ui.adapters.UpcomingBillsAdapter
import com.example.cmpt362_finalproject.data.UserPreferenceDatabase
import com.example.cmpt362_finalproject.data.UserPreferenceRepository

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var billsAdapter: UpcomingBillsAdapter
    private lateinit var viewModel: HomeViewModel
    private val billsList = mutableListOf<BillModel>()

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val database = UserPreferenceDatabase.getInstance(requireContext())
        val repository = UserPreferenceRepository(database.userPreferenceDao)
        viewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]
        
        viewModel.userPreferences.observe(viewLifecycleOwner) { preferences ->
            preferences?.let {
                binding.welcomeMessage.text = "Welcome ${it.userName}"
                binding.textViewSavingsGoal.text = "0 / $${it.savingsGoal}"
                binding.textViewSpendingChallenge.text = "0 / $${it.spendingChallenge}"
            } ?: run {
                binding.welcomeMessage.text = "Welcome"
                binding.textViewSavingsGoal.text = "0 / $0.0"
                binding.textViewSpendingChallenge.text = "0 / $0.0"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Handle results from AddBillActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_BILL && resultCode == Activity.RESULT_OK) {
            val name = data?.getStringExtra("bill_name") ?: return
            val dueDate = data.getStringExtra("due_date") ?: return
            val amount = data.getStringExtra("amount") ?: return
            addBill(BillModel(name, dueDate, amount)) // Add new bill
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

    companion object {
        private const val REQUEST_CODE_ADD_BILL = 1
    }
}