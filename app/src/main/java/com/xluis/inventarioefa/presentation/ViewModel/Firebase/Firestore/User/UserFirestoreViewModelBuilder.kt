package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserFirestoreRepository

object UserFirestoreViewModelBuilder {
    fun getUserFirestoreViewModelFactory () =
        UserFirestoreViewModelFactory(UserFirestoreRepository(FirebaseFirestore.getInstance()))
}