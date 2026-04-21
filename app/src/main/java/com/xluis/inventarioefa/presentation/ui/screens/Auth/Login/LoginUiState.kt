package com.xluis.inventarioefa.presentation.ui.screens.Auth.Login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val showRecoverPasswordDialog: Boolean = false,
    val isLoading: Boolean = false,
    val recoveryEmail: String = "",
    val recoveryError: String? = null
)