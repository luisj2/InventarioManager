package com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements

sealed class ArticleMovement(
    open var id: String? = null,
    open var baseId: String? = null,
    open val articleId: String = "",
    open val articleName: String = "",
    open val articleCategory: String = "",
    open val userName: String = "",
    open val userId: String = "",
    open val date: String = ""
)
