package com.xluis.inventarioefa._domain.UseCases.Firebase.Auth

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthLoginRepository

class LoginUserUseCase(
    private val authLoginRepository: AuthLoginRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): ValidationResult {
        return authLoginRepository.logIn(email, password)
            .onSuccess { UserDataStore.saveUserUid(it) }
            .toValidationResult("No se ha podido completar el login")
    }
}
