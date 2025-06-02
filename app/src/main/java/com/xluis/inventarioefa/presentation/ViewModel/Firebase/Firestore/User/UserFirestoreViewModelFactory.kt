package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserFirestoreRepository

class UserFirestoreViewModelFactory (private val repository : UserFirestoreRepository) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserFirestoreViewModel::class.java)){
            return UserFirestoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}