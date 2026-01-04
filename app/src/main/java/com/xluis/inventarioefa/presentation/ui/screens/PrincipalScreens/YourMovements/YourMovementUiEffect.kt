package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements

sealed class YourMovementUiEffect {
    data class ShowToast (val message : String) : YourMovementUiEffect()
}