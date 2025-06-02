package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import java.io.IOException

object FirestoreExceptionHandler {

    private const val TAG = "FirestoreExceptionHandler"

    fun handle(e: Exception): String {
        return when (e) {
            is FirebaseFirestoreException -> {
                when (e.code) {
                    FirebaseFirestoreException.Code.UNAVAILABLE -> "No tienes conexión a internet"
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> "No tienes permiso para acceder a la base de datos"
                    FirebaseFirestoreException.Code.NOT_FOUND -> "No se encontró el recurso solicitado"
                    FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> "Tiempo de espera agotado, intenta de nuevo"
                    FirebaseFirestoreException.Code.ABORTED -> "La operación fue abortada, inténtalo nuevamente"
                    else -> "Error en Firestore: ${e.message}"
                }
            }
            is IOException -> "Problemas de red, verifica tu conexión"
            else -> {
                Log.e(TAG, "Error inesperado: ${e.message}", e)
                "Error inesperado"
            }
        }
    }
}