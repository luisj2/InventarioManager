package com.xluis.inventarioefa.data.Database.Firebase.Auth

import com.google.firebase.auth.FirebaseAuth
import com.xluis.inventarioefa._domain.Repository.Firebase.Auth.AuthLoginQuery
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthLoginRepository(private val auth: FirebaseAuth) : AuthLoginQuery {

    override suspend fun logIn(
        email: String,
        password: String
    ): SuspendResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("No se obtuvo UID")
                SuspendResult.Success(uid)
            } catch (e: Exception) {
                SuspendResult.Error(AuthExceptionHandler.handle(e))
            }
        }
    }
}
