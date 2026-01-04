package com.xluis.inventarioefa.presentation.ui.screens.Auth.Register

data class RegisterUiState(
    val userName : String = "",
    val email : String = "",
    val password : String = "",
    val isLoading : Boolean = false,
    val userNameError : String? = null,
    val emailError : String? = null,
    val passwordError : String? = null
)
