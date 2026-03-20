package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article

sealed class ZoneInfoUiEffect {
    data class NavigateToZone(val zoneId: String) : ZoneInfoUiEffect()

    data class ShowToast(val message: String) : ZoneInfoUiEffect()

    data object OpenSVGSelector : ZoneInfoUiEffect()

    data class RemoveArticleList(val articlesIdToRemove: List<String>) : ZoneInfoUiEffect()

    data class RemoveArticleInSaveList (val articleId : String) : ZoneInfoUiEffect()

    data class RemoveMovementInSaveList (val movementId : String) : ZoneInfoUiEffect()
    data class ChangeMovementCount(val movementId: String, val newCount: Int) : ZoneInfoUiEffect()


    data class AddMovementToSaveList(val movementList: List<ArticleMovement>) : ZoneInfoUiEffect()

    data class AddArticleToSave(val article: Article) : ZoneInfoUiEffect()



    data object ClearToSaveLists : ZoneInfoUiEffect()

    data object NavigateToArticleSelector : ZoneInfoUiEffect()

    data object NavigateToZoneSelector : ZoneInfoUiEffect()

    data object NavigateBack : ZoneInfoUiEffect()

    data object NavigateBackShowDialog : ZoneInfoUiEffect()
}