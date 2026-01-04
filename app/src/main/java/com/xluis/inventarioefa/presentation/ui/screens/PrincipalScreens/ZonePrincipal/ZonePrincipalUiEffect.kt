package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal

import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

sealed class ZonePrincipalUiEffect {
    data class NavigateToCreateZone(val parentId : String?,val storageType : StorageType) : ZonePrincipalUiEffect()

    data class NavigateToZoneInfo(val zoneId : String,val storageType : String) : ZonePrincipalUiEffect()

    data object NavigateToSettingScreen : ZonePrincipalUiEffect()

    data class ShowToast (val message : String) : ZonePrincipalUiEffect()
}