package com.xluis.inventarioefa.domain.model.Database.Firebase.Auth

import com.google.firebase.auth.FirebaseAuth
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AuthRepository(private val auth: FirebaseAuth) : AuthQuery {

    companion object {
        private const val ERROR = "FirebaseAuthError"
    }

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null
    override fun getLoggedUserEmail(): String? = auth.currentUser?.email
    override fun logOut() {
        auth.signOut()
    }


    override suspend fun registerUser(email: String, password: String): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                SuspendResult.Success(true)
            } catch (e: Exception) {
               SuspendResult.Error(AuthExceptionHandler.handle(e))
            }
        }
    }


    override suspend fun logIn(email: String, password: String): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(AuthExceptionHandler.handle(e))
            }
        }
    }



}