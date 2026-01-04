package com.xluis.inventarioefa.data.Database.Firebase.Auth

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.io.IOException

object AuthExceptionHandler {

    private const val TAG = "AuthExceptionHandler"

    fun handle(e: Exception): String {
        return when (e) {
            is FirebaseAuthUserCollisionException -> "El usuario ya existe"

            is FirebaseAuthInvalidUserException -> "El usuario no existe o está deshabilitado"

            is FirebaseAuthInvalidCredentialsException -> {
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "El correo electrónico es inválido"
                    "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta"
                    else -> "Credenciales inválidas"
                }
            }

            is FirebaseNetworkException -> "No tienes conexión a internet"

            is FirebaseAuthException -> {
                when (e.errorCode) {
                    "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil"
                    "ERROR_USER_DISABLED" -> "La cuenta ha sido deshabilitada"
                    else -> "Error de autenticación"
                }
            }

            is IOException -> "Problemas en la red, verifica tu conexión"

            else -> {
                Log.e(TAG, "Error inesperado: ${e.message}", e)
                "Error inesperado"
            }
        }
    }

}