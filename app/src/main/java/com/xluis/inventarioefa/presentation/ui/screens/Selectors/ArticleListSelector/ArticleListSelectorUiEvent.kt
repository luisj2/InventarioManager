package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector

import ArticleCategory
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.Enums.SortType

sealed class ArticleListSelectorUiEvent {
    data object GetAllArticles : ArticleListSelectorUiEvent()

    data class InitValues (val storageType : String,val zoneId:String, val screenId : String) : ArticleListSelectorUiEvent()

    data class ToggleCreateArticleDialog (val state : Boolean) : ArticleListSelectorUiEvent()

    data object SaveSelectedArticles : ArticleListSelectorUiEvent()

    data class AddArticle (val article : Article) : ArticleListSelectorUiEvent()

    data class ShowToast (val message : String) : ArticleListSelectorUiEvent()

    data class SelectArticleList (val article : Article, val count : Int): ArticleListSelectorUiEvent()

    data class DeselectArticleByIdList (val articleId : String) : ArticleListSelectorUiEvent()

    data class OnSearchQueryChanged (val query : String) : ArticleListSelectorUiEvent()

    data class OnCategoryChanged (val category  : ArticleCategory?) : ArticleListSelectorUiEvent()

    data class OnAddSortType (val sortSelected : SortType) : ArticleListSelectorUiEvent()

    data class OnRemoveSortType (val sortType : SortType) : ArticleListSelectorUiEvent()

    data object NavigateBack : ArticleListSelectorUiEvent()
}