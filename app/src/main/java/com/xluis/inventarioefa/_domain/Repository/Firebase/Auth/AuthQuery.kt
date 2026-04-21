package com.xluis.inventarioefa._domain.Repository.Firebase.Auth

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface AuthQuery {
    suspend fun logIn(
        email: String, password: String
    ): SuspendResult<String>

    suspend fun registerUser(email: String, password: String): SuspendResult<String>

    suspend fun sendPasswordResetEmail (email : String) : SuspendResult<Boolean>

}