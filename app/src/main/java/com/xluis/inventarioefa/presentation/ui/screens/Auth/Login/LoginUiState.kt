package com.xluis.inventarioefa.presentation.ui.screens.Auth.Login

data class LoginUiState (
    val email : String = "",
    val password : String = "",
    val emailError : String? = null,
    val passwordError : String? = null,
    val isLoading : Boolean = false
)