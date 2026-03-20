package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen

import com.xluis.inventarioefa._domain.model.User.ZoneRequest

sealed class ZoneRequestsUiEvent {

    data object ChargeUserZoneRequests : ZoneRequestsUiEvent()

    data class AcceptZoneRequest (val zoneRequest : ZoneRequest) : ZoneRequestsUiEvent()

    data class RejectZoneRequest (val requestId : String) : ZoneRequestsUiEvent()

}