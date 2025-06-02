package com.xluis.inventarioefa.domain.model.Database.Firebase.Auth

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface AuthQuery {


    fun isUserLoggedIn() : Boolean

    fun getLoggedUserEmail () : String?

    fun logOut ()


    suspend fun registerUser(email : String,password : String): SuspendResult<Boolean>

    suspend fun logIn(email : String,password : String): SuspendResult<Boolean>
}