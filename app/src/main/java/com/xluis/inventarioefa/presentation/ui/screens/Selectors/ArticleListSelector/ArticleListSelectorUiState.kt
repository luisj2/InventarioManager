package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector

import ArticleCategory
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.Enums.SortType
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

data class ArticleListSelectorUiState (
    val articleList : List<Article> = emptyList(),
    val filteredArticleList : List<Article> = emptyList(),
    val zoneId : String = "",
    val screenId : String = "",
    val userId : String? = null,
    val storageType : StorageType = StorageType.FIREBASE,
    val createArticleDialogState : Boolean = false,
    val selectedArticleList : List<Article> = emptyList(),
    val isLoading : Boolean = false,
    val searchQuery : String = "",
    val selectedCategory : ArticleCategory? = null,
    val sortList : List<SortType> = emptyList()

)
