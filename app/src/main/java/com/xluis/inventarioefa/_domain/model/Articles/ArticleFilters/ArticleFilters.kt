package com.xluis.inventarioefa.domain.model.DataClass.Articles.ArticleFilters

data class ArticleFilters(
    val category : String? = null,
    val ubication : String? = null,
    val state : String? = null,
    val sortBy : ArticleSortOptions? = null
    )
