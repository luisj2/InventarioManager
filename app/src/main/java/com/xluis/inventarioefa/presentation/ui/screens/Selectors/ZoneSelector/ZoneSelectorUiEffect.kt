package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector

sealed class ZoneSelectorUiEffect {

    data object NavigateBack : ZoneSelectorUiEffect()

    data class ShowToast (val message : String) : ZoneSelectorUiEffect()

}