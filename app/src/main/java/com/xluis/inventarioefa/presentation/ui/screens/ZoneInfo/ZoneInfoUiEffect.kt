package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

sealed class ZoneInfoUiEffect {
    data class NavigateToZone(val zoneId: String) : ZoneInfoUiEffect()

    data class ShowToast (val message : String) : ZoneInfoUiEffect()

    data object OpenSVGSelector : ZoneInfoUiEffect()

    data object NavigateToArticleSelector : ZoneInfoUiEffect()

    data object NavigateToZoneSelector : ZoneInfoUiEffect()

    data object NavigateBack : ZoneInfoUiEffect()

    data object NavigateBackShowDialog : ZoneInfoUiEffect()
}