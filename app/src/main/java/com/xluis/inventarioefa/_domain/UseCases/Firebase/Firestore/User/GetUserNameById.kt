package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User

import com.xluis.inventarioefa.data.Database.Firestore.User.UserFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserNameById(
    private val userRepository: UserFirestoreRepository
) {
    suspend operator fun invoke(
        userId : String
    ) : SuspendResult<String>{
        return userRepository.getUserNameById(userId)
    }
}