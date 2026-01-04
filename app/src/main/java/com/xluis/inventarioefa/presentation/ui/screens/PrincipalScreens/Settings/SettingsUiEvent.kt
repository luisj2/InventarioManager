package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings

sealed class SettingsUiEvent {
    data object OnLoginClicked : SettingsUiEvent()
    data object OnLogoutClicked : SettingsUiEvent()
    data object InitSession : SettingsUiEvent()

    data object NavigateToLogin : SettingsUiEvent()
}