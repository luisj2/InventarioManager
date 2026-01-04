package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings

sealed class SettingUiEffect {
    data object NavigateToLogin : SettingUiEffect()
    data object NavigateBack : SettingUiEffect()
    data class ShowToast(val message: String) : SettingUiEffect()
}