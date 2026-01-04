package com.xluis.inventarioefa.presentation.ui.screens.Auth.Login

sealed class LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()

    data object LoginClicked : LoginUiEvent()
}