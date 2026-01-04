package com.xluis.inventarioefa._domain.UseCases.Firebase.Auth

import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthSessionRepository

class Logout(
    private val sessionRepository: AuthSessionRepository
) {
    suspend operator fun invoke(){
        sessionRepository.logOut()
        UserDataStore.clearUserUid()
    }
}