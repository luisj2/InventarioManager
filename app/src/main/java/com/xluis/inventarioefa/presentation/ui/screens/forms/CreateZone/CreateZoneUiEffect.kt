package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

sealed class CreateZoneUiEffect {

    data object NavigateBack : CreateZoneUiEffect()

    data object NavigateToZoneSelector : CreateZoneUiEffect()
    data class ShowToast (val message : String) : CreateZoneUiEffect()

    data object ClearScreenArticleList : CreateZoneUiEffect()

    data class RemoveArticle (val articleId : String) : CreateZoneUiEffect()

    data class DeleteArticleInList (val articleId : String) : CreateZoneUiEffect()

}