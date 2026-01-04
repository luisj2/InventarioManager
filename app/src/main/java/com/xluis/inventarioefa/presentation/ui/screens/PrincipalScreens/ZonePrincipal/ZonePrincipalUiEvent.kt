package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.domain.model.DataClass.Zone.Filters.ZoneSortOptions

sealed class ZonePrincipalUiEvent {

    data object UpdateUserZoneList : ZonePrincipalUiEvent()

    data class OnChangeQuery  (val query : String) :ZonePrincipalUiEvent()

    data class OnAddFilter (val filter : ZoneSortOptions) : ZonePrincipalUiEvent()

    data class OnRemoveFilter (val filter : ZoneSortOptions) : ZonePrincipalUiEvent()


    data class NavigateToCreateLocalZone(val parentId : String?) : ZonePrincipalUiEvent()
    data class NavigateToCreateFirebaseZone(val parentId : String?) : ZonePrincipalUiEvent()

    data class NavigateToZoneInfo (val zoneId : String,val storageType : String) : ZonePrincipalUiEvent()

    data class ToggleZoneDeleteSelection (val zone : Zone) : ZonePrincipalUiEvent()

    data class ToggleDeleteDialogState (val state : Boolean) : ZonePrincipalUiEvent()

    data object ClearSelectedDeleteZoneList : ZonePrincipalUiEvent()

    data object CloseCreateZoneMenu : ZonePrincipalUiEvent()

    data object OpenCreateZoneMenu : ZonePrincipalUiEvent()

    data object ActivateSelectZoneToRemoveMode : ZonePrincipalUiEvent()
    data object DesactivateSelectZoneToRemoveMode : ZonePrincipalUiEvent()

    data object DeleteSelectedZones : ZonePrincipalUiEvent()
}