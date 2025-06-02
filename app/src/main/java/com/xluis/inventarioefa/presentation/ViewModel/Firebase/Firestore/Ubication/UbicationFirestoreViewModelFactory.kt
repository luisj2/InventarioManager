package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.UbicationFirestoreRepository

class UbicationFirestoreViewModelFactory(private val repository: UbicationFirestoreRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UbicationFirestoreViewModel::class.java)) {
            return UbicationFirestoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}