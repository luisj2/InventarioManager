package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth

import com.google.firebase.auth.FirebaseAuth
import com.xluis.inventarioefa.domain.model.Database.Firebase.Auth.AuthRepository

object AuthViewModelBuilder {
    fun getAuthViewModelFactory() = AuthViewModelFactory(AuthRepository(FirebaseAuth.getInstance()))
}