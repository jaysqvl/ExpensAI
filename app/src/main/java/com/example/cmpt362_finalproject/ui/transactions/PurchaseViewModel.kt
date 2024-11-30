package com.example.cmpt362_finalproject.ui.transactions


import androidx.lifecycle.*
import java.lang.IllegalArgumentException

class PurchaseViewModel(private val repository: PurchaseRepository) : ViewModel() {
    val allCommentsLiveData: LiveData<List<Entry>> = repository.allPurchases.asLiveData()

    fun insert(entry: Entry) {
        repository.insert(entry)
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