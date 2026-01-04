package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector

import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

sealed class ZoneSelectorUiEvent {
    data object GetZoneListByUserId : ZoneSelectorUiEvent()


    data class GetArticleById (val zoneId : String,val articleId : String) : ZoneSelectorUiEvent()

    data class SelectZone(val zoneIdSelected : String, val storageType : StorageType) : ZoneSelectorUiEvent()
    data object DeselectZone : ZoneSelectorUiEvent()

    data object SaveMoveArticleMovement  : ZoneSelectorUiEvent()
    data class ShowToast (val message : String) : ZoneSelectorUiEvent()

    data object NavigateBack : ZoneSelectorUiEvent()

    data class Initialize(
        val zoneIdFromMove: String,
        val zoneToMoveStorageType : StorageType,
        val articleIdToMove: String,
        val articleCountToMove: Int
    ) : ZoneSelectorUiEvent()

}