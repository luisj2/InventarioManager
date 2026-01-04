package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.domain.model.DataClass.Zone.Filters.ZoneSortOptions

data class ZonePrincipalUiState(
    val isLoading : Boolean = false,
    val searchQuery : String = "",
    val filters : List<ZoneSortOptions> = emptyList(),
    val isRefreshing : Boolean = false,
    val showDeleteZonesDialog : Boolean = false,
    val userId : String? = null,
    val selectionMode : Boolean = false,
    val selectionToRemoveZones : Set<Zone> = setOf(),
    val allZoneList : List<Zone> = emptyList(),
    val filteredZones : List<Zone> = emptyList(),
    val isCreateZoneMenuOpen : Boolean = false
)