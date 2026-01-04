package com.xluis.inventarioefa._domain.UseCases.Firebase.Auth

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthRegisterRepository
import com.xluis.inventarioefa.data.Database.Firestore.User.UserFirestoreRepository
import com.xluis.inventarioefa.data.Model.User.UserFirestore

class RegisterAndSaveUserUseCase(
    private val authRepository: AuthRegisterRepository,
    private val userFirestoreRepository: UserFirestoreRepository
) {
    suspend operator fun invoke(
        user: UserFirestore,
        password: String
    ): ValidationResult {
        return authRepository.registerUser(user.email, password)
            .flatMap { uid->
                userFirestoreRepository.insertUser(user.copy(uid = uid))
            }.toValidationResult()
    }
}
