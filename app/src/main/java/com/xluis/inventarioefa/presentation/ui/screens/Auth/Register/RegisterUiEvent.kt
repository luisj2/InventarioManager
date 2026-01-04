package com.xluis.inventarioefa.presentation.ui.screens.Auth.Register

sealed class RegisterUiEvent {
    data class UserNameChanged(val userName: String) : RegisterUiEvent()
    data class EmailChanged(val email: String) : RegisterUiEvent()
    data class PasswordChanged(val password: String) : RegisterUiEvent()
    data object RegisterClicked : RegisterUiEvent()
}
