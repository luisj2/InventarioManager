package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

data class ZoneSelectorUiState(
    val zoneList: List<Zone> = emptyList(),          // Lista de zonas del usuario
    val articleToMove: Article? = null,             // Artículo que se va a mover
    val zoneIdSelected: String? = null, // Zona seleccionada para mover el artículo
    val storageTypeZoneSelected : StorageType? = null,
    val isLoading: Boolean = false,                 // Indicador de carga
    val userId: String? = null,                         // Usuario actual
    val zoneIdFromMove: String = "", // Zona de origen del artículo
    val zoneFromMoveStorageType : StorageType? = null,
    val articleIdToMove: String = "",               // ID del artículo a mover
    val articleCountToMove: Int = 0                 // Cantidad del artículo a mover
)
