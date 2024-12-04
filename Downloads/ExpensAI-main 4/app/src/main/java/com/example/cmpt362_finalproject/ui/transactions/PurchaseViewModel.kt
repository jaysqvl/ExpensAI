package com.example.cmpt362_finalproject.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import java.lang.IllegalArgumentException
import kotlinx.coroutines.launch

class PurchaseViewModel(private val repository: PurchaseRepository) : ViewModel() {
    val allCommentsLiveData: LiveData<List<Entry>> = repository.allPurchases.asLiveData()
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun insert(entry: Entry) {
        viewModelScope.launch {
            try {
                repository.insert(entry)
            } catch (e: Exception) {
                _error.value = "Failed to save transaction: ${e.message}"
            }
        }
    }

    fun deleteFirst(){
        val purchaseList = allCommentsLiveData.value
        if (purchaseList != null && purchaseList.size > 0){
            val id = purchaseList[0].id
            repository.delete(id)
        }
    }

    fun deleteAll(){
        val purchaseList = allCommentsLiveData.value
        if (purchaseList != null && purchaseList.size > 0)
            repository.deleteAll()
    }
}

class PurchaseViewModelFactory (private val repository: PurchaseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(PurchaseViewModel::class.java))
            return PurchaseViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}