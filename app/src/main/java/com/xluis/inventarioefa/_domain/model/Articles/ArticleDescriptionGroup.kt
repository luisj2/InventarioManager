package com.xluis.inventarioefa._domain.model.Articles

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article

data class ArticleDescriptionGroup(
    val article: Article,
    val descriptions: List<String>
)