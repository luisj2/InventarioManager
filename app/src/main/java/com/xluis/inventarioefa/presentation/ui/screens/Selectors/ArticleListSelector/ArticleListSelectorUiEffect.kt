package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector

sealed class ArticleListSelectorUiEffect {

    data object NavigateBack : ArticleListSelectorUiEffect()

    data class ShowToast (val message : String) : ArticleListSelectorUiEffect()
}