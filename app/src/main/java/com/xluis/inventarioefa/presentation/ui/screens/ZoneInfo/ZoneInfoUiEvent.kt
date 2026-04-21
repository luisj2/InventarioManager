package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.Zone.ZoneMember

sealed class ZoneInfoUiEvent {

    data class UpdateZone(val zoneId: String) : ZoneInfoUiEvent()

    data object OpenSVGSelector : ZoneInfoUiEvent()

    data class ImportArticles(val articleList: List<Article>) : ZoneInfoUiEvent()

    data class ToggleShareDialogState(val state: Boolean) : ZoneInfoUiEvent()

    data class ToggleShowMoveDialog(val state : Boolean) : ZoneInfoUiEvent()


    data class ShowToast(val message: String) : ZoneInfoUiEvent()

    data object InitArticlesAndMovementsList : ZoneInfoUiEvent()

    data class InitValues(val storageType: String,val screenId : String) : ZoneInfoUiEvent()

    data object InitToSaveListsFlows : ZoneInfoUiEvent()

    data class NavigateToZone(val zoneId: String) : ZoneInfoUiEvent()

    data object NavigateToArticleSelector : ZoneInfoUiEvent()

    data object NavigateToZoneSelector : ZoneInfoUiEvent()

    data object NavigateBack : ZoneInfoUiEvent()

    data object SaveChanges : ZoneInfoUiEvent()

    data class AddMemeberByEmail(val email: String) : ZoneInfoUiEvent()

    data object ShowConfirmLeftDialog : ZoneInfoUiEvent()

    data object DissmissConfirmLeftDialog : ZoneInfoUiEvent()

    data object ShowAddMovementMenu : ZoneInfoUiEvent()

    data object DissmissAddMovementMenu : ZoneInfoUiEvent()

    data object ShowQuantityDialog : ZoneInfoUiEvent()
    data object DissmissQuantityDialog : ZoneInfoUiEvent()

    data object ShowZoneNameDialog : ZoneInfoUiEvent()
    data object DismissZoneNameDialog : ZoneInfoUiEvent()

    data class ChangeZoneName (val newZoneName : String) : ZoneInfoUiEvent()

    data class AddRemoveQuantity (val isAdd : Boolean, val quantity : Int) : ZoneInfoUiEvent()
    data class UpdateArticlesToSaveList(val articleToSaveList: List<Article>) : ZoneInfoUiEvent()
    data class UpdateMovementsToSaveList(val movementsToSaveList: List<ArticleMovement>) :
        ZoneInfoUiEvent()

    data class UpdateArticleToModifyQuantity (val articleToModify : Article) : ZoneInfoUiEvent()

    data object RemoveArticlesById : ZoneInfoUiEvent()

    data object ClearToSaveLists : ZoneInfoUiEvent()


    data class RemoveArticleToSave(val articleId: String) : ZoneInfoUiEvent()

    data class RemoveArticleInSaveList (val article : Article): ZoneInfoUiEvent()
    data class RemoveMovementInSaveList (val movementId : String) : ZoneInfoUiEvent()

    data class ChangeMovementCount(val movementId: String, val newCount: Int) : ZoneInfoUiEvent()

    data class ChangeArticleCount(val articleId: String, val articleCount: Int) : ZoneInfoUiEvent()

    data class QuantityChanged (val article : Article) : ZoneInfoUiEvent()


    data class AddMovementToSaveList(val movementList: List<ArticleMovement>) : ZoneInfoUiEvent()

    data class AddArticleToSave(val article: Article) : ZoneInfoUiEvent()

    data class SelectArticle(val articleId: String) : ZoneInfoUiEvent()

    data class DeselectArticle(val idDeselect: String) : ZoneInfoUiEvent()

    data object RemoveMember : ZoneInfoUiEvent()

    data class ShowRemoveMemberDialog (val memberSelected : ZoneMember) : ZoneInfoUiEvent()
    data object DissmissRemoveMemberDialog : ZoneInfoUiEvent()

    data class SetMoveCount (val newCount : Int) : ZoneInfoUiEvent()

    data class SetMoveArticle (val articleToMove : Article) : ZoneInfoUiEvent()
    data object MoveSelectedArticle : ZoneInfoUiEvent()

    data class ToggleSelectionDeleteArticleMode(val state: Boolean) : ZoneInfoUiEvent()


    data object ClearArticleDeleteSelections : ZoneInfoUiEvent()

    data object RemoveArticlesToSave : ZoneInfoUiEvent()


    data object NavigateBackShowDialog : ZoneInfoUiEvent()

    data object ShowDeleteArticleDialog : ZoneInfoUiEvent()
    data object DismissDeleteArticleDialog : ZoneInfoUiEvent()


}