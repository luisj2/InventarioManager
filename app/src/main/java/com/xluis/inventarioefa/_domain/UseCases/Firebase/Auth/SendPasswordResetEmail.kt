package com.xluis.inventarioefa._domain.UseCases.Firebase.Auth

import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class SendPasswordResetEmail(
 private val repository : AuthRepository
) {
    suspend operator fun invoke(email : String) : SuspendResult<Boolean>{
        return repository.sendPasswordResetEmail(email)
    }
}