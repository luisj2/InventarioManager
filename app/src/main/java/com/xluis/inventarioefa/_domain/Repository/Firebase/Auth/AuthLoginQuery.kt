package com.xluis.inventarioefa._domain.Repository.Firebase.Auth

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface AuthLoginQuery {
    suspend fun logIn(
        email: String, password: String
    ): SuspendResult<String>
}