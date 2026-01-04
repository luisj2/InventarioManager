package com.xluis.inventarioefa._domain.UseCases.Firebase.Auth

import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthSessionRepository

class IsUserLoggedIn(
    private val sessionRepository: AuthSessionRepository
) {

    operator fun invoke() : Boolean{
        return sessionRepository.isUserLoggedIn()
    }
}