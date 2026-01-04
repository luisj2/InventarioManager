package com.xluis.inventarioefa._domain.Repository.Firebase.Auth

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface AuthRegisterQuery {
    suspend fun registerUser(email: String, password: String): SuspendResult<String>

}