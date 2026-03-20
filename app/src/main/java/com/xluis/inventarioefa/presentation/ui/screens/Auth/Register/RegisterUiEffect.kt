package com.xluis.inventarioefa.presentation.ui.screens.Auth.Register

sealed class RegisterUiEffect {
    data class ShowToast (val message : String) : RegisterUiEffect()

    data class NavigateToLogin (val email : String,val password : String) : RegisterUiEffect()

}