package com.xluis.inventarioefa.presentation.ui.screens.Auth.Register

sealed class RegisterUiEffect {
    data class ShowToast (val message : String) : RegisterUiEffect()

    data object NavigateToLogin : RegisterUiEffect()

}