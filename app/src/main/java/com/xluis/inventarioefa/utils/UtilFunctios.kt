package com.xluis.inventarioefa.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.core.os.bundleOf
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.navigateTo(destinationClass: Class<out Activity>, bundle: Bundle = bundleOf()) {
    val activityContext = this as? Activity

    if (activityContext != null) {
        val intent = Intent(this, destinationClass).apply {
            putExtras(bundle)
        }
        activityContext.startActivity(intent)
    } else {
        val intent = Intent(this, destinationClass).apply {
            putExtras(bundle)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}

fun Context.hasConexion(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}



fun String.parseDate () : Date{
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.parse(this) ?: Date(0)
    } catch (e: Exception) {
        Date(0)
    }
}


fun Array<out SuspendResult<*>>.handleErrors(showMessage: (String) -> Unit) {
    this.forEach { result ->
        if (result is SuspendResult.Error) {
            showMessage(result.message)
        }
    }
}


fun Context.toast(message: String, lenght: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, lenght).show()
}

fun List<MutableState<*>>.cleanFields() {
    forEach { state ->
        when (state.value) {
            is String -> (state as MutableState<String>).value = ""
            is Int -> (state as MutableState<Int>).value = 1
            else -> (state as MutableState<Any?>).value = null
        }
    }
}
fun isValidEmail(email: String?): Boolean {
    if (email.isNullOrBlank()) return false
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.+)(\\.)(.+)"
    return Regex(emailRegex).matches(email)
}
fun validatePassword(password: String?): ValidationResult {
    if (password.isNullOrBlank()) {
        return ValidationResult.Error("La contraseña no puede estar vacía")
    }

    val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    return if (!Regex(passwordRegex).matches(password)) {
        ValidationResult.Error(
            "La contraseña debe tener al menos 8 caracteres e incluir letras y números"
        )
    } else {
        ValidationResult.Success
    }
}










