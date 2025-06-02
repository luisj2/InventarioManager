package com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleFilters

data class ArticleFilters(
    val category : String? = null,
    val ubication : String? = null,
    val state : String? = null,
    val sortBy : SortOptions? = null
    )
