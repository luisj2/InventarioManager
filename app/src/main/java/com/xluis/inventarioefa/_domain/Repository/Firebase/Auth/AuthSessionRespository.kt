package com.xluis.inventarioefa.domain.model.Database.Firebase.Auth

interface AuthSessionRespository {


    fun isUserLoggedIn(): Boolean

    fun getLoggedUserEmail(): String?

    fun logOut()

}