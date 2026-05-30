package com.xluis.inventarioefa._domain.model.Articles

data class ArticleDescriptionGroup(
    val articleId: String,
    val articleName: String,
    val count: Int,
    val descriptions: List<String>
)