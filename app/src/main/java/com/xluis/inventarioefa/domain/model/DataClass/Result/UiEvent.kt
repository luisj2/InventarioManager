package com.xluis.inventarioefa.domain.model.DataClass.Result

sealed class UiEvent {
    data class ShowToast (val message : String) : UiEvent()
    data class ShowActionMessage(val message : String) : UiEvent()
    data object NavigateBack : UiEvent()
    data object NavigateToInventaryData : UiEvent()
}