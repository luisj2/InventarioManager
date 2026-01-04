package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings

data class SettingsUiState (
    val isLoggedIn : Boolean = false,
    val userId : String? = null,
    val username : String? = null,
    val isLoading : Boolean = false
)