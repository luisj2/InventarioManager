package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article

sealed class ZoneInfoUiEvent {

    data class UpdateZone(val zoneId: String) : ZoneInfoUiEvent()

    data object OpenSVGSelector : ZoneInfoUiEvent()

    data class ImportArticles (val articleList : List<Article>) : ZoneInfoUiEvent()

    data class ToggleShareDialogState (val state : Boolean) : ZoneInfoUiEvent()

    data class ShowToast (val message : String) : ZoneInfoUiEvent()

    data object InitArticlesAndMovementsList : ZoneInfoUiEvent()

    data class UpdateStorageType (val storageType : String) : ZoneInfoUiEvent()

    data class NavigateToZone(val zoneId: String) : ZoneInfoUiEvent()

    data object NavigateToArticleSelector : ZoneInfoUiEvent()

    data object NavigateToZoneSelector : ZoneInfoUiEvent()

    data object NavigateBack : ZoneInfoUiEvent()

    data class SaveChanges(val zoneId: String) : ZoneInfoUiEvent()

    data class ShareZone (val email : String): ZoneInfoUiEvent()

    data object ShowConfirmLeftDialog : ZoneInfoUiEvent()

    data object DissmissConfirmLeftDialog : ZoneInfoUiEvent()

    data object ShowAddMovementMenu : ZoneInfoUiEvent()

    data object DissmissAddMovementMenu : ZoneInfoUiEvent()
    data class AddArticlesToSave (val articlesToSaveList : List<Article>) : ZoneInfoUiEvent()

    data class AddArticleToSave (val article : Article) : ZoneInfoUiEvent()

    data class AddMovementToSave (val movement : ArticleMovement) : ZoneInfoUiEvent()

    data object RemoveArticlesById : ZoneInfoUiEvent()

    data class AddMovementsToSave (val movementsToSave : List<ArticleMovement>) : ZoneInfoUiEvent()

    data class RemoveArticleToSave (val articleId : String) : ZoneInfoUiEvent()
    data class RemoveMovementToSave (val articleId : String) : ZoneInfoUiEvent()

    data class SelectArticle(val articleId : String) : ZoneInfoUiEvent()

    data class DeselectArticle (val idDeselect : String): ZoneInfoUiEvent()

    data class RemoveMember (val memberId : String) : ZoneInfoUiEvent()

    data class MoveSelectedArticle (val selectedArticle : Article) : ZoneInfoUiEvent()

    data class ToggleSelectionDeleteArticleMode (val state : Boolean) : ZoneInfoUiEvent()

    data object ClearArticleDeleteSelections : ZoneInfoUiEvent()

    data object ClearArticlesAndMovementsToSave :ZoneInfoUiEvent()

    data object NavigateBackShowDialog  : ZoneInfoUiEvent()

    data object ShowDeleteArticleDialog : ZoneInfoUiEvent()
    data object DismissDeleteArticleDialog : ZoneInfoUiEvent()







}