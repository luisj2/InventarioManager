package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen

import com.xluis.inventarioefa._domain.model.User.ZoneRequest

data class ZoneRequestsUiState(
    val isLoading : Boolean = false,
    val requestsList : List<ZoneRequest> = emptyList(),
    val userId : String? = null
)
