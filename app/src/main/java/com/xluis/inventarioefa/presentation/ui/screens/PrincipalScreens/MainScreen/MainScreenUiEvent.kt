package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.MainScreen

import com.xluis.inventarioefa.presentation.ui.navigation.PrincipalScreen

sealed class MainScreenUiEvent {
    data class ChangeScreenClick (val screen : PrincipalScreen) : MainScreenUiEvent()
}