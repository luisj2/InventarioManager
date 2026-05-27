package com.xluis.inventarioefa.presentation.ui.screens.Auth.Login

sealed class LoginUiEffect {
    data object navigateToMainScreen : LoginUiEffect()

    data class ShowToast (val message : String) : LoginUiEffect()
}