package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

sealed class CreateZoneUiEffect {

    data object NavigateBack : CreateZoneUiEffect()

    data object NavigateToZoneSelector : CreateZoneUiEffect()
    data class ShowToast (val message : String) : CreateZoneUiEffect()

}