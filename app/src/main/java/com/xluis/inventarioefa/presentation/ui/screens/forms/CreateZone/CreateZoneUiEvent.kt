package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

import androidx.compose.ui.graphics.vector.ImageVector
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

sealed class CreateZoneUiEvent {

    data object InitParentListByUserId  : CreateZoneUiEvent()

    data class ShowToast (val message : String) : CreateZoneUiEvent()
    data class InitValues (val parentId : String?,val storageType : StorageType) : CreateZoneUiEvent()
    data class IconChanged(val icon: ImageVector) : CreateZoneUiEvent()
    data class ZoneNameChanged(val zoneName: String) : CreateZoneUiEvent()
    data class ZoneParentIdChanged(val parentId: String) : CreateZoneUiEvent()
    data object CreateZoneClicked : CreateZoneUiEvent()

    data class AddArticle(val articleEntity : ArticleEntity) : CreateZoneUiEvent()

    data object NavigateBack : CreateZoneUiEvent()

    data object NavigateToZoneSelector : CreateZoneUiEvent()

    data object OpenIconSelector : CreateZoneUiEvent()

    data object CloseIconSelector : CreateZoneUiEvent()

    data object NavigateNextPage : CreateZoneUiEvent()
    data object NavigatePreviousPage : CreateZoneUiEvent()

    data object OpenCreateArticleDialog : CreateZoneUiEvent()
    data object CloseCreateArticleDialog : CreateZoneUiEvent()

}