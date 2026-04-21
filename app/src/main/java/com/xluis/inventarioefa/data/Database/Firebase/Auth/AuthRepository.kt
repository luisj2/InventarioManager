package com.xluis.inventarioefa.data.Database.Firebase.Auth

import com.google.firebase.auth.FirebaseAuth
import com.xluis.inventarioefa._domain.Repository.Firebase.Auth.AuthQuery
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) : AuthQuery, BaseAuthRepository() {
    override suspend fun logIn(email: String, password: String): SuspendResult<String> {
        return executeAuthOperation {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("No se obtuvo UID")
            uid
        }
    }

    override suspend fun registerUser(email: String, password: String): SuspendResult<String> {
        return executeAuthOperation {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("No se obtuvo UID")
            auth.signOut()
            uid
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): SuspendResult<Boolean> {
        return executeAuthOperation {
            auth.sendPasswordResetEmail(email).await()
            true
        }
    }


}