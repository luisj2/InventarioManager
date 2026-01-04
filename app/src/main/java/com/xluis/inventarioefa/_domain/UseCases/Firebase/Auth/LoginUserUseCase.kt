package com.xluis.inventarioefa._domain.UseCases.Firebase.Auth

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthLoginRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class LoginUserUseCase(
    private val authLoginRepository: AuthLoginRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): ValidationResult {
        val result = authLoginRepository.logIn(email, password)

        if (result is SuspendResult.Success) {
            UserDataStore.saveUserUid(result.data)
        }

        return result.toValidationResult("No se ha podido completar el login")
    }
}
