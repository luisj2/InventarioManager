package com.xluis.inventarioefa._domain.model.DataClass.User

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult

data class User(
    val id : String = "",
    val email: String = "",
    val userName: String = "",
    val zonesIdList: List<String> = emptyList()
) {
    fun validateEmail(): ValidationResult {
        if (email.isBlank()) return ValidationResult.Error("El email no puede estar vacío")
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        if (!Regex(emailRegex).matches(email)) return ValidationResult.Error(
            "Formato de correo inválido. Debe contener:\n" +
                    "- Un nombre de usuario (letras, números o . _ % + -)\n" +
                    "- El símbolo @\n" +
                    "- Un dominio válido"
        )
        return ValidationResult.Success
    }

    fun validateUserName(): ValidationResult {
        if(userName.isBlank()) return ValidationResult.Error("El nombre de usuario no puede estar vacío")
        if(userName.length < 3) return ValidationResult.Error("El nombre de usuario debe de tener al menos 3 caracteres")
        return ValidationResult.Success
    }
}
