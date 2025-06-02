package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.UbicationFirestoreRepository

object UbicationFirestoreViewModelBuilder {
    fun getUbicationFirestoreViewModelFactory() =
        UbicationFirestoreViewModelFactory(UbicationFirestoreRepository(FirebaseFirestore.getInstance()))
}