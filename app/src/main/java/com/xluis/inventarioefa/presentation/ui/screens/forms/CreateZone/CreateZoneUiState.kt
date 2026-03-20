package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

data class CreateZoneUiState (
    val icon : ImageVector = Icons.Default.Home,
    val parentList : List<ZoneSummary> = emptyList(),
    val noParentSelectedText : String = "Ninguna",
    val isParentPredefined : Boolean = false,
    val showCreateArticleDialog : Boolean = false,
    val storageType: StorageType = StorageType.LOCAL,
    val userId : String? = null,
    val zoneName : String = "",
    val zoneNameError : String? = null,
    val parentZoneId : String? = null,
    val parentName : String? = null,
    val isLoading : Boolean = false,
    val isIconSelectorOpen : Boolean = false,
    val page : Int = 1
)