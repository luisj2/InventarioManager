package com.xluis.inventarioefa.data.Database.Room

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import java.io.IOException

object RoomExceptionHandler {

    private const val TAG = "RoomExceptionHandler"

    fun handle(e: Exception): String {
        return when (e) {
            is SQLiteConstraintException -> "Violación de restricción en la base de datos"
            is SQLiteException -> "Error en la base de datos: ${e.message}"
            is IOException -> "Problemas de entrada/salida, verifica tu almacenamiento"
            is IllegalStateException -> "Estado inválido: ${e.message}"
            else -> {
                Log.e(TAG, "Error inesperado en Room: ${e.message}", e)
                "Error inesperado en la base de datos"
            }
        }
    }
}