package com.xluis.inventarioefa.data.Database.Firebase.Auth

import com.google.firebase.auth.FirebaseAuth
import com.xluis.inventarioefa.domain.model.Database.Firebase.Auth.AuthSessionRespository


class AuthSessionRepository(private val auth: FirebaseAuth) : AuthSessionRespository {

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null
    override fun getLoggedUserEmail(): String? = auth.currentUser?.email
    override fun logOut() {
        auth.signOut()
    }
}