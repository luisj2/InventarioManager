package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article

sealed class ArticleListSelectorUiEffect {

    data object NavigateBack : ArticleListSelectorUiEffect()

    data class ShowToast (val message : String) : ArticleListSelectorUiEffect()
    data class ArticlesSaved (val selectedArticles : List<Article>,val selectedMovements : List<ArticleMovement>) : ArticleListSelectorUiEffect()
}