package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen

sealed class ZoneRequestsUiEffect {

    data class showToast (val message : String) : ZoneRequestsUiEffect()
}