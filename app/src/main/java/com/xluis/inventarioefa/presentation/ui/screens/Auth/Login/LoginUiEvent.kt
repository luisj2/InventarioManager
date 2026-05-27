package com.xluis.inventarioefa.presentation.ui.screens.Auth.Login

sealed class LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()

    data object ShowRecoverPasswordDialog : LoginUiEvent()

    data object DismissRecoverPasswordDialog : LoginUiEvent()

    data class RecoveryEmailChanged (val email : String) : LoginUiEvent()

    data object SendResetEmail : LoginUiEvent()

    data object LoginClicked : LoginUiEvent()

    data object NavigateToMainScreen : LoginUiEvent()
}