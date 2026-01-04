package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User

import com.xluis.inventarioefa.data.Database.Firestore.User.UserFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserIdByEmail (
    private val userRepository: UserFirestoreRepository
) {
    suspend operator fun invoke(
        email : String
    ) : SuspendResult<String>{
        return userRepository.getIdByEmail(email)
    }
}